package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.firefly.api.model.generated.Account;
import de.adorsys.opba.firefly.api.model.generated.Transaction;
import de.adorsys.opba.firefly.api.model.generated.TransactionSplit;
import de.adorsys.opba.fireflyexporter.client.FireflyAccountsApiClient;
import de.adorsys.opba.fireflyexporter.client.FireflyTransactionsApiClient;
import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.entity.TransactionExportJob;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.fireflyexporter.repository.TransactionExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;
import de.adorsys.opba.tpp.ais.api.model.generated.Balance;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireFlyTransactionExporter {

    private final ApiConfig apiConfig;
    private final TppAisClient aisApi;
    private final TransactionOperations txOper;
    private final OpenBankingConfig bankingConfig;
    private final FireFlyTokenProvider tokenProvider;
    private final FireflyTransactionsApiClient transactionsApi;
    private final BankConsentRepository consentRepository;
    private final TransactionExportJobRepository exportJobRepository;

    @Async
    public void exportToFirefly(String fireFlyToken, long exportJobId, String bankId, List<String> accountsTransactionsToExport, LocalDate from, LocalDate to) {
        tokenProvider.setToken(fireFlyToken);
        int numExported = 0;
        int numErrored = 0;
        String lastError = null;
        for (String accountIdToExport : accountsTransactionsToExport) {
            try {
                exportAccountsTransactionsToFireFly(bankId, accountIdToExport, from, to);
            } catch (Exception ex) {
                log.error("Failed to export account: {}", accountIdToExport, ex);
                numErrored++;
                lastError = ex.getMessage();
            }

            numExported++;
            updateAccountsExported(exportJobId, numExported, numErrored, accountsTransactionsToExport.size(), lastError);
        }

        txOper.execute(callback -> {
            TransactionExportJob toUpdate = exportJobRepository.getOne(exportJobId);
            toUpdate.setCompleted(true);
            return exportJobRepository.save(toUpdate);
        });
    }

    private void exportAccountsTransactionsToFireFly(String bankId, String accountIdToExport, LocalDate from, LocalDate to) {
        ResponseEntity<TransactionsResponse> transactions = aisApi.getTransactions(
                accountIdToExport,
                bankingConfig.getDataProtectionPassword(),
                bankingConfig.getUserId(),
                apiConfig.getRedirectOkUri(UUID.randomUUID().toString()),
                apiConfig.getRedirectNokUri(),
                UUID.randomUUID(),
                null,
                null,
                null,
                bankId,
                null,
                consentRepository.findFirstByBankIdOrderByModifiedAt(bankId).map(BankConsent::getConsentId).orElse(null),
                from,
                to,
                null,
                "both",
                false
        );

        AccountReport report = transactions.getBody().getTransactions();
        for (TransactionDetails transaction : report.getBooked()) {
            Transaction fireflyTransaction = new Transaction();
            TransactionSplit split = new TransactionSplit();
            split.setAmount(transaction.getTransactionAmount().getAmount());
            split.setCurrencyCode(transaction.getTransactionAmount().getCurrency());
            split.setSourceIban(transaction.getCreditorAccount().getIban());
            split.setDestinationIban(transaction.getDebtorAccount().getIban());
            transactionsApi.storeTransaction(fireflyTransaction);
        }
    }

    private void updateAccountsExported(long exportJobId, int numExported, int numErrored, int toExport, String lastError) {
        txOper.execute(callback -> {
            TransactionExportJob exportJob = exportJobRepository.getOne(exportJobId);
            exportJob.setAccountsExported(numExported);
            exportJob.setNumAccountsErrored(numErrored);
            exportJob.setLastErrorMessage(lastError);
            exportJob.setNumAccountsToExport(toExport);
            return exportJobRepository.save(exportJob);
        });
    }
}
