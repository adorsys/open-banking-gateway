package de.adorsys.opba.fireflyexporter.service;

import com.google.common.collect.ImmutableList;
import de.adorsys.opba.firefly.api.model.generated.Transaction;
import de.adorsys.opba.firefly.api.model.generated.TransactionSplit;
import de.adorsys.opba.fireflyexporter.client.FireflyTransactionsApiClient;
import de.adorsys.opba.fireflyexporter.client.TppAisClient;
import de.adorsys.opba.fireflyexporter.config.ApiConfig;
import de.adorsys.opba.fireflyexporter.config.OpenBankingConfig;
import de.adorsys.opba.fireflyexporter.dto.ExportableAccount;
import de.adorsys.opba.fireflyexporter.entity.BankConsent;
import de.adorsys.opba.fireflyexporter.entity.TransactionExportJob;
import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import de.adorsys.opba.fireflyexporter.repository.TransactionExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReference;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireFlyTransactionExporter {

    private final ApiConfig apiConfig;
    private final TppAisClient aisApi;
    private final TransactionOperations txOper;
    private final OpenBankingConfig bankingConfig;
    private final FireFlyTokenProvider tokenProvider;
    private final ExportableAccountService exportableAccounts;
    private final FireflyTransactionsApiClient transactionsApi;
    private final BankConsentRepository consentRepository;
    private final TransactionCategorizer categorizer;
    private final TransactionExportJobRepository exportJobRepository;

    @Async
    @SuppressWarnings("checkstyle:MethodLength") // Method length is mostly from long argument list to API call
    public void exportToFirefly(String fireFlyToken, long exportJobId, String bankId, List<String> accountsTransactionsToExport, LocalDate from, LocalDate to) {
        tokenProvider.setToken(fireFlyToken);
        Set<String> availableAccountsInFireFlyByIban = exportableAccounts.exportableAccounts(fireFlyToken, bankId).getBody().stream()
                .map(ExportableAccount::getIban)
                .collect(Collectors.toSet());

        int numExported = 0;
        int numErrored = 0;
        AtomicInteger numTransactionsExported = new AtomicInteger();
        AtomicInteger numTransactionsErrored = new AtomicInteger();
        String lastError = null;
        for (String accountIdToExport : accountsTransactionsToExport) {
            try {
                exportAccountsTransactionsToFireFly(
                        exportJobId,
                        bankId,
                        accountIdToExport,
                        from,
                        to,
                        numTransactionsExported,
                        numTransactionsErrored,
                        availableAccountsInFireFlyByIban
                );
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

    @SuppressWarnings("checkstyle:MethodLength") // Method length is mostly from long argument list to API call
    private void exportAccountsTransactionsToFireFly(
            long exportJobId,
            String bankId,
            String accountIdToExport,
            LocalDate from,
            LocalDate to,
            AtomicInteger numTransactionsExported,
            AtomicInteger numTransactionsErrored,
            Set<String> availableAccountsInFireFlyByIban
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
                consentRepository.findFirstByBankIdOrderByModifiedAtDesc(bankId).map(BankConsent::getConsentId).orElse(null),
                "",
                from,
                to,
                null,
                "both",
                false,
                null,
                null,
                null,
                null
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
                exportFireFlyTransaction(transaction, availableAccountsInFireFlyByIban);
                numTransactionsExported.incrementAndGet();
            } catch (Exception ex) {
                log.error("Failed to export transaction: {}", transaction.getTransactionId(), ex);
                numTransactionsErrored.incrementAndGet();
                lastError = ex.getMessage();
            }

            updateTransactionsExported(exportJobId, numTransactionsExported.get(), numTransactionsErrored.get(), lastError);
        }
    }

    private void exportFireFlyTransaction(TransactionDetails transaction, Set<String> availableAccountsInFireFlyByIban) {
        Transaction fireflyTransaction = new Transaction();
        fireflyTransaction.setErrorIfDuplicateHash(true);
        TransactionSplit split = new TransactionSplit();
        split.setSepaCtId(transaction.getEndToEndId());
        split.setInternalReference(transaction.getTransactionId());
        split.setDescription(buildDescription(transaction));
        LocalDate txDate = null != transaction.getValueDate() ? transaction.getValueDate() : LocalDate.now();
        split.setDate(DateTimeFormatter.ISO_DATE.format(txDate));
        if (null != transaction.getBookingDate()) {
            split.setBookDate(DateTimeFormatter.ISO_DATE.format(transaction.getBookingDate()));
        }

        BigDecimal transactionAmount = new BigDecimal(transaction.getTransactionAmount().getAmount());
        if (null != transaction.getDebtorAccount() && availableAccountsInFireFlyByIban.contains(transaction.getDebtorAccount().getIban())) {
            parseTransactionAmount(transaction.getDebtorAccount(), transaction.getCreditorAccount(), transactionAmount, split);
        } else {
            transactionAmount = transactionAmount.compareTo(BigDecimal.ZERO) < 0 ? transactionAmount : transactionAmount.negate();
            parseTransactionAmount(transaction.getCreditorAccount(), transaction.getDebtorAccount(), transactionAmount, split);
        }

        split.setCurrencyCode(transaction.getTransactionAmount().getCurrency());
        try {
            split.setCategoryName(categorizer.categorizeTransaction(transaction));
        } catch (RuntimeException ex) {
            log.warn("Failed to categorize transaction {}", transaction.getTransactionId(), ex);
        }

        fireflyTransaction.setTransactions(ImmutableList.of(split));

        transactionsApi.storeTransaction(fireflyTransaction);
    }

    private void parseTransactionAmount(AccountReference debtor, AccountReference creditor, BigDecimal transactionAmount, TransactionSplit split) {
        if (transactionAmount.compareTo(BigDecimal.ZERO) < 0) {
            split.setAmount(transactionAmount.negate().toPlainString());
            split.setType(TransactionSplit.TypeEnum.WITHDRAWAL);
            split.setSourceName(debtor.getIban());
            split.setSourceIban(debtor.getIban());
            split.setDestinationIban(null != creditor ? creditor.getIban() : null);
        } else {
            split.setAmount(transactionAmount.toPlainString());
            split.setType(TransactionSplit.TypeEnum.DEPOSIT);
            split.setSourceIban(null != creditor ? creditor.getIban() : null);
            split.setDestinationName(debtor.getIban());
            split.setDestinationIban(debtor.getIban());
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
