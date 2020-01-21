package de.adorsys.opba.tppbankingapi.ais.resource;

import de.adorsys.opba.protocol.services.ais.account.Account;
import de.adorsys.opba.protocol.services.ais.account.AccountsReport;
import de.adorsys.opba.tppbankingapi.ais.model.AccountDetails;
import de.adorsys.opba.tppbankingapi.ais.model.AccountList;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AccountListMapperTest {
    public static final AccountListMapper ACCOUNT_LIST_MAPPER = Mappers.getMapper(AccountListMapper.class);

    @Test
    void toAccountList() {
        AccountsReport accountsReport = AccountsReportProducer.produceAccountReport();
        assertNotNull(accountsReport);

        AccountList list = ACCOUNT_LIST_MAPPER.toAccountList(accountsReport);

        assertEquals(2, list.getAccounts().size());

        Account givenAccount = accountsReport.getAccounts().get(0);
        AccountDetails producedAccountDetails = list.getAccounts().get(0);

        assertEquals(givenAccount.getCurrency().getCurrencyCode(), producedAccountDetails.getCurrency());
        assertEquals(givenAccount.getUsage(), producedAccountDetails.getUsage().toString());
        assertEquals(givenAccount.getCashAccountType(), producedAccountDetails.getCashAccountType());
        assertEquals(givenAccount.getIban(), producedAccountDetails.getIban());
        assertEquals(givenAccount.getLinkedAccounts(), producedAccountDetails.getLinkedAccounts());
        assertEquals(givenAccount.getName(), producedAccountDetails.getName());
        assertEquals(givenAccount.getProduct(), producedAccountDetails.getProduct());
        assertEquals(givenAccount.getResourceId(), producedAccountDetails.getResourceId());
        assertEquals(givenAccount.getStatus(), producedAccountDetails.getStatus().toString());
    }

}
