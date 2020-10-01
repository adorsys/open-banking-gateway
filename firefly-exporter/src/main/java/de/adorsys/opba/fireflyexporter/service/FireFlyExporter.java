package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.firefly.api.model.generated.Account;
import de.adorsys.opba.fireflyexporter.client.FireflyAccountsApiClient;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.Balance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireFlyExporter {

    private static final String OPBA_ID_PREFIX = "OPBA-ID:";
    // Format is: Mon Sep 17 03:00:00 EEST 2018
    private static final DateTimeFormatter FIREFLY_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");

    private final TransactionOperations txOper;
    private final FireFlyTokenProvider tokenProvider;
    private final FireflyAccountsApiClient accountsApi;
    private final AccountExportJobRepository exportJobRepository;

    @Async
    public void exportToFirefly(String fireFlyToken, long exportJobId, AccountList accountList) {
        tokenProvider.setToken(fireFlyToken);
        int numExported = 0;
        int numErrored = 0;
        String lastError = null;
        for (AccountDetails account : accountList.getAccounts()) {
            try {
                exportAccountToFireFly(account);
            } catch (Exception ex) {
                log.error("Failed to export account: {}", account.getResourceId(), ex);
                numErrored++;
                lastError = ex.getMessage();
            }

            numExported++;
            updateAccountsExported(exportJobId, numExported, numErrored, accountList.getAccounts().size(), lastError);
        }

        txOper.execute(callback -> {
            AccountExportJob toUpdate = exportJobRepository.getOne(exportJobId);
            toUpdate.setCompleted(true);
            return exportJobRepository.save(toUpdate);
        });
    }

    private void exportAccountToFireFly(AccountDetails account) {
        Account fireflyAccount = new Account();
        fireflyAccount.setName(account.getIban());
        fireflyAccount.setIban(account.getIban());
        fireflyAccount.setCurrencyCode(account.getCurrency());
        fireflyAccount.setBic(account.getBic());
        fireflyAccount.setActive(true);
        fireflyAccount.setAccountRole(Account.AccountRoleEnum.DEFAULTASSET);
        fireflyAccount.setType(Account.TypeEnum.ASSET);
        fireflyAccount.setNotes(OPBA_ID_PREFIX + account.getResourceId());

        Balance available = getInterimAvailableBalance(account);
        if (null != available) {
            // currentBalance is read-only property
            fireflyAccount.setOpeningBalance(Double.valueOf(available.getBalanceAmount().getAmount()));
            LocalDate referenceDate = null != available.getReferenceDate() ? LocalDate.parse(available.getReferenceDate()) : LocalDate.now();
            fireflyAccount.setOpeningBalanceDate(referenceDate.atStartOfDay().atZone(ZoneOffset.UTC).format(FIREFLY_DATE_FORMAT));
        }

        accountsApi.storeAccount(fireflyAccount);
    }

    private Balance getInterimAvailableBalance(AccountDetails account) {
        return account.getBalances()
                .stream().filter(it -> "interimavailable".equalsIgnoreCase(it.getBalanceType()))
                .findFirst()
                .orElse(null);
    }

    private void updateAccountsExported(long exportJobId, int numExported, int numErrored, int toExport, String lastError) {
        txOper.execute(callback -> {
            AccountExportJob exportJob = exportJobRepository.getOne(exportJobId);
            exportJob.setAccountsExported(numExported);
            exportJob.setNumAccountsErrored(numErrored);
            exportJob.setLastErrorMessage(lastError);
            exportJob.setNumAccountsToExport(toExport);
            return exportJobRepository.save(exportJob);
        });
    }
}
