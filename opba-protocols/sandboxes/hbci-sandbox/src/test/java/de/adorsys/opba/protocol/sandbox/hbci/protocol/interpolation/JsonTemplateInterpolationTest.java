package de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonTemplateInterpolationTest {

    private final JsonTemplateInterpolation tested = new JsonTemplateInterpolation(new ObjectMapper());

    @Test
    void testHbciCreation() {
        HbciSandboxContext ctx = new HbciSandboxContext();
        Bank bank = new Bank();
        bank.setBic("CODE");
        bank.setBlz("1234");
        ctx.setBank(bank);

        String result = tested.interpolateToHbci("response-templates/wrong-tan.json", ctx);

        assertThat(result).isNotBlank();
    }

    @Test
    void testSimpleInterpolation() {
        HbciSandboxContext ctx = new HbciSandboxContext();
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
        HbciSandboxContext ctx = new HbciSandboxContext();
        Account account = new Account();
        account.setNumber("1999");
        User user = new User();
        user.setAccounts(ImmutableList.of(account));
        ctx.setUser(user);

        Map<String, String> result = tested.interpolate("interpolation/loop.json", ctx);

        assertThat(result).containsEntry("foo", "123");
        assertThat(result).containsEntry("prefix", "1999");
        assertThat(result).containsEntry("bar", "99");
    }

    @Test
    void testLoopInterpolationTwoAccounts() {
        HbciSandboxContext ctx = new HbciSandboxContext();
        Account accountOne = new Account();
        accountOne.setNumber("account_1");
        Account accountTwo = new Account();
        accountTwo.setNumber("account_2");
        User user = new User();
        user.setAccounts(ImmutableList.of(accountOne, accountTwo));
        ctx.setUser(user);

        Map<String, String> result = tested.interpolate("interpolation/loop.json", ctx);

        assertThat(result).containsEntry("foo", "123");
        assertThat(result).containsEntry("prefix", "account_1");
        assertThat(result).containsEntry("prefix_2", "account_2");
        assertThat(result).containsEntry("bar", "99");
    }
}