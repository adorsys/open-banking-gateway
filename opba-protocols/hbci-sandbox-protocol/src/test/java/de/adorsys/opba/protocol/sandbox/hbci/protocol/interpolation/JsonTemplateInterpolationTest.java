package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonTemplateInterpolationTest {

    private final JsonTemplateInterpolation tested = new JsonTemplateInterpolation(new ObjectMapper());

    @Test
    void testSimpleInterpolation() {
        SandboxContext ctx = new SandboxContext();
        Bank bank = new Bank();
        bank.setBic("CODE");
        bank.setBlz("1234");
        ctx.setBank(bank);

        Map<String, String> result = tested.interpolate("interpolation/simple.json", ctx);

        assertThat(result).containsEntry("foo", "123");
        assertThat(result).containsEntry("prefix_1234", "CODE");
    }

    @Test
    void testLoopInterpolationSingleAccount() {
        SandboxContext ctx = new SandboxContext();
        Account account = new Account();
        account.setNumber("1999");
        ctx.setAccounts(ImmutableList.of(account));

        Map<String, String> result = tested.interpolate("interpolation/loop.json", ctx);

        assertThat(result).containsEntry("foo", "123");
        assertThat(result).containsEntry("prefix_1", "1999");
        assertThat(result).containsEntry("bar", "99");
    }

    @Test
    void testLoopInterpolationTwoAccounts() {
        SandboxContext ctx = new SandboxContext();
        Account accountOne = new Account();
        accountOne.setNumber("account_1");
        Account accountTwo = new Account();
        accountTwo.setNumber("account_2");
        ctx.setAccounts(ImmutableList.of(accountOne, accountTwo));

        Map<String, String> result = tested.interpolate("interpolation/loop.json", ctx);

        assertThat(result).containsEntry("foo", "123");
        assertThat(result).containsEntry("prefix_1", "account_1");
        assertThat(result).containsEntry("prefix_2", "account_2");
        assertThat(result).containsEntry("bar", "99");
    }
}