package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import de.adorsys.xs2a.adapter.api.model.AccountAccess;
import de.adorsys.xs2a.adapter.api.model.AdditionalInformationAccess;
import lombok.Data;

import java.util.List;


@Data
public class AccountAccessLog {

    private List<AccountReferenceLog> accounts;
    private List<AccountReferenceLog> balances;
    private List<AccountReferenceLog> transactions;
    private AdditionalInformationAccess additionalInformation;
    private AccountAccess.AvailableAccounts availableAccounts;
    private AccountAccess.AvailableAccountsWithBalance availableAccountsWithBalance;
    private AccountAccess.AllPsd2 allPsd2;
    private List<String> restrictedTo;
}
