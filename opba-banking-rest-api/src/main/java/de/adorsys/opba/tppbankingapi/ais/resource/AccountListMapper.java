package de.adorsys.opba.tppbankingapi.ais.resource;

import de.adorsys.opba.tppbanking.services.ais.account.Account;
import de.adorsys.opba.tppbanking.services.ais.account.AccountsReport;
import de.adorsys.opba.tppbankingapi.ais.model.AccountDetails;
import de.adorsys.opba.tppbankingapi.ais.model.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.AccountStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AccountListMapper {
    AccountList toAccountList(AccountsReport accountsReport) {
        AccountList accountList = new AccountList();
        accountList.setAccounts(accountsReport.getAccounts().stream()
                    .map(this::toAccountDetails)
                    .collect(Collectors.toList()));
        return accountList;
    }

    private AccountDetails toAccountDetails(Account account) {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setResourceId(account.getResourceId());
        accountDetails.setIban(account.getIban());
        accountDetails.setCurrency(account.getCurrency() != null ? account.getCurrency().toString() : null);
        accountDetails.setProduct(account.getProduct());
        accountDetails.setCashAccountType(account.getCashAccountType());
        accountDetails.setStatus(AccountStatus.valueOf(StringUtils.upperCase(account.getStatus())));
        accountDetails.setLinkedAccounts(account.getLinkedAccounts());
        accountDetails.setUsage(AccountDetails.UsageEnum.valueOf(StringUtils.upperCase(account.getUsage())));
        return accountDetails;
    }
}
