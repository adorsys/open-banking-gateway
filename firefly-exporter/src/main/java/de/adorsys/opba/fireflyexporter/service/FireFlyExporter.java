package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.firefly.api.model.generated.Account;
import de.adorsys.opba.fireflyexporter.client.FireflyAccountsApiClient;
import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

@Service
@RequiredArgsConstructor
public class FireFlyExporter {

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
            fireflyAccount.setIban(account.getIban());
            fireflyAccount.setNotes("ID:" + account.getResourceId());
            fireflyAccount.setCurrencyCode(account.getCurrency());
            fireflyAccount.setBic(account.getBic());
            fireflyAccount.setName(account.getName());
            fireflyAccount.setCurrentBalance(
                    account.getBalances()
                            .stream().filter(it -> "interimAvailable".equals(it.getBalanceType()))
                            .findFirst()
                            .map(it -> it.getBalanceAmount().getAmount())
                            .orElse(null)
            );
            accountsApi.storeAccount(fireflyAccount);
            numExported++;
            updateAccountsExported(exportJob, numExported, accountList.getAccounts().size());
        }
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
