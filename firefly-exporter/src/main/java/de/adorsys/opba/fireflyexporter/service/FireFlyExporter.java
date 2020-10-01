package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.firefly.api.model.generated.Account;
import de.adorsys.opba.fireflyexporter.client.FireflyAccountsApiClient;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import de.adorsys.opba.tpp.ais.api.model.generated.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
    public void exportToFirefly(String fireFlyToken, AccountExportJob exportJob, AccountList accountList) {
        tokenProvider.setToken(fireFlyToken);
        int numExported = 0;
        for (AccountDetails account : accountList.getAccounts()) {
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
            numExported++;
            updateAccountsExported(exportJob, numExported, accountList.getAccounts().size());
        }
    }

    private Balance getInterimAvailableBalance(AccountDetails account) {
        return account.getBalances()
                .stream().filter(it -> "interimavailable".equalsIgnoreCase(it.getBalanceType()))
                .findFirst()
                .orElse(null);
    }

    private void updateAccountsExported(AccountExportJob exportJob, int numExported, int toExport) {
        txOper.execute(callback -> {
            exportJobRepository.findById(exportJob.getId());
            exportJob.setAccountsExported(numExported);
            exportJob.setNumAccountsToExport(toExport);
            return exportJobRepository.save(exportJob);
        });
    }
}
