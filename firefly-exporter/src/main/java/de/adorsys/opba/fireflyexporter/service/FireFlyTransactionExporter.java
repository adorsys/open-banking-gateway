package de.adorsys.opba.fireflyexporter.service;

import com.google.common.collect.ImmutableList;
import de.adorsys.opba.firefly.api.model.generated.Transaction;
import de.adorsys.opba.firefly.api.model.generated.TransactionSplit;
import de.adorsys.opba.fireflyexporter.client.FireflyTransactionsApiClient;
import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.entity.TransactionExportJob;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.fireflyexporter.repository.TransactionExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static de.adorsys.opba.fireflyexporter.service.Consts.FIREFLY_DATE_FORMAT;

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
        AtomicInteger numTransactionsExported = new AtomicInteger();
        AtomicInteger numTransactionsErrored = new AtomicInteger();
        String lastError = null;
        for (String accountIdToExport : accountsTransactionsToExport) {
            try {
                exportAccountsTransactionsToFireFly(exportJobId, bankId, accountIdToExport, from, to, numTransactionsExported, numTransactionsErrored);
            } catch (Exception ex) {
                log.error("Failed to export account: {}", accountIdToExport, ex);
                numErrored++;
                lastError = ex.getMessage();
            }

            numExported++;

            updateAccountsExported(
                    exportJobId,
                    numExported,
                    numErrored,
                    accountsTransactionsToExport.size(),
                    numTransactionsExported.get(),
                    numTransactionsErrored.get(),
                    lastError
            );
        }

        txOper.execute(callback -> {
            TransactionExportJob toUpdate = exportJobRepository.getOne(exportJobId);
            toUpdate.setCompleted(true);
            return exportJobRepository.save(toUpdate);
        });
    }

    private void exportAccountsTransactionsToFireFly(
            long exportJobId,
            String bankId,
            String accountIdToExport,
            LocalDate from,
            LocalDate to,
            AtomicInteger numTransactionsExported,
            AtomicInteger numTransactionsErrored
    ) {
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

        if (transactions.getStatusCode() == HttpStatus.ACCEPTED) {
            throw new IllegalStateException("Consent is required, but was missing, try to import accounts or click on import transactions button again");
        }

        if (transactions.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Unexpected status code: " + transactions.getStatusCode().toString());
        }

        AccountReport report = transactions.getBody().getTransactions();
        String lastError = null;
        for (TransactionDetails transaction : report.getBooked()) {
            try {
                exportFireFlyTransaction(transaction);
                numTransactionsExported.incrementAndGet();
            } catch (Exception ex) {
                log.error("Failed to export transaction: {}", transaction.getTransactionId(), ex);
                numTransactionsErrored.incrementAndGet();
                lastError = ex.getMessage();
            }

            updateTransactionsExported(exportJobId, numTransactionsExported.get(), numTransactionsErrored.get(), lastError);
        }
    }

    private void exportFireFlyTransaction(TransactionDetails transaction) {
        Transaction fireflyTransaction = new Transaction();
        fireflyTransaction.setErrorIfDuplicateHash(true);
        TransactionSplit split = new TransactionSplit();
        split.setSepaCtId(transaction.getEndToEndId());
        split.setImportHashV2(transaction.getTransactionId());
        split.setDescription(buildDescription(transaction));
        LocalDate txDate = null != transaction.getValueDate() ? transaction.getValueDate() : LocalDate.now();
        split.setDate(txDate.atStartOfDay().atZone(ZoneOffset.UTC).format(FIREFLY_DATE_FORMAT));
        if (null != transaction.getBookingDate()) {
            split.setBookDate(transaction.getBookingDate().atStartOfDay().atZone(ZoneOffset.UTC).format(FIREFLY_DATE_FORMAT));
        }

        parseTransactionAmount(transaction, split);
        split.setCurrencyCode(transaction.getTransactionAmount().getCurrency());
        fireflyTransaction.setTransactions(ImmutableList.of(split));

        transactionsApi.storeTransaction(fireflyTransaction);
    }

    private void parseTransactionAmount(TransactionDetails transaction, TransactionSplit split) {
        BigDecimal amount = new BigDecimal(transaction.getTransactionAmount().getAmount());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            split.setAmount(amount.negate().toPlainString());
            split.setType(TransactionSplit.TypeEnum.WITHDRAWAL);
            split.setSourceName(transaction.getDebtorAccount().getIban());
            split.setSourceIban(transaction.getDebtorAccount().getIban());
            split.setDestinationIban(transaction.getCreditorAccount().getIban());
        } else {
            split.setAmount(transaction.getTransactionAmount().getAmount());
            split.setType(TransactionSplit.TypeEnum.DEPOSIT);
            split.setSourceIban(transaction.getCreditorAccount().getIban());
            split.setDestinationName(transaction.getDebtorAccount().getIban());
            split.setDestinationIban(transaction.getDebtorAccount().getIban());
        }
    }

    private String buildDescription(TransactionDetails transaction) {
        StringBuilder result = new StringBuilder();
        if (Strings.isNotBlank(transaction.getRemittanceInformationStructured())) {
            result.append(transaction.getRemittanceInformationStructured());
        }
        if (Strings.isNotBlank(transaction.getRemittanceInformationUnstructured())) {
            result.append(transaction.getRemittanceInformationUnstructured());
        }
        if (Strings.isNotBlank(transaction.getAdditionalInformation())) {
            result.append(transaction.getAdditionalInformation());
        }
        String description = result.toString();
        return Strings.isBlank(description) ? "Exported from OPBA" : description;
    }

    private void updateAccountsExported(
            long exportJobId, int numExported, int numErrored, int toExport, int numTransactionsExported, int numTransactionsErrored, String lastError
    ) {
        txOper.execute(callback -> {
            TransactionExportJob exportJob = exportJobRepository.getOne(exportJobId);
            exportJob.setNumAccountsErrored(numErrored);
            exportJob.setNumTransactionsExported(numExported);
            exportJob.setNumAccountsToExport(toExport);
            if (null != lastError) {
                exportJob.setLastErrorMessage(lastError);
            }
            exportJob.setNumTransactionsExported(numTransactionsExported);
            exportJob.setNumTransactionsErrored(numTransactionsErrored);
            return exportJobRepository.save(exportJob);
        });
    }

    private void updateTransactionsExported(long exportJobId, int numTransactionsExported, int numTransactionsErrored, String lastError) {
        txOper.execute(callback -> {
            TransactionExportJob exportJob = exportJobRepository.getOne(exportJobId);
            if (null != lastError) {
                exportJob.setLastErrorMessage(lastError);
            }
            exportJob.setNumTransactionsExported(numTransactionsExported);
            exportJob.setNumTransactionsErrored(numTransactionsErrored);
            return exportJobRepository.save(exportJob);
        });
    }
}
