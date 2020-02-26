package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.xs2a.mapper.generated.Xs2aToFacadeMapperImpl;
import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.Amount;
import de.adorsys.xs2a.adapter.service.model.Balance;
import de.adorsys.xs2a.adapter.service.model.CashAccountType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Xs2AToFacadeMapperTest {
    static String IBAN = "DE23445343434";
    static String AMOUNTVALUE = "123,32";

    @Test
    public void testMapping() {

        AccountListHolder xs2aEntity = new AccountListHolder();
        {
            Amount amount = new Amount();
            amount.setAmount(AMOUNTVALUE);
            Balance balance = new Balance();
            balance.setBalanceAmount(amount);
            List<Balance> balances = new ArrayList<>();
            balances.add(balance);

            AccountDetails account = new AccountDetails();
            account.setIban(IBAN);
            account.setBalances(balances);
            account.setCashAccountType(CashAccountType.CASH);

            List<AccountDetails> accounts = new ArrayList<>();
            accounts.add(account);

            xs2aEntity.setAccounts(accounts);
        }
        AccountListBody facadeEntity = new Xs2aToFacadeMapperImpl().map(xs2aEntity);
        Assert.assertEquals(IBAN, facadeEntity.getAccounts().get(0).getIban());
        Assert.assertEquals(CashAccountType.CASH.name(), facadeEntity.getAccounts().get(0).getCashAccountType());
    }
}
