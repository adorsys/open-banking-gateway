package de.adorsys.opba.tppbanking.services.ais;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tppbanking.services.ais.account.AccountsReport;
import de.adorsys.opba.tppbanking.services.ais.account.GetAccountsService;
import de.adorsys.opba.tppbanking.services.ais.account.GetAccountsServiceMockImpl;
import de.adorsys.opba.tppbanking.services.psuconsentsession.PsuConsentSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GetAccountsServiceMockImplTest.TestConfiguration.class)
class GetAccountsServiceMockImplTest {
    @Autowired
    private GetAccountsService getAccountsServiceMock;

    @Test
    void getAccountsFor() {
        Optional<AccountsReport> accounts = getAccountsServiceMock.getAccountsFor(new PsuConsentSession());
        assertTrue(accounts.isPresent());
        assertNotNull(accounts.get().getAccounts());
        assertEquals(2, accounts.get().getAccounts().size());
    }

    @ContextConfiguration
    public static class TestConfiguration {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); //Registers all modules on classpath
            return mapper;
        }

        @Bean
        public GetAccountsService getAccountsService(ObjectMapper objectMapper) {
            return new GetAccountsServiceMockImpl(objectMapper);
        }
    }
}
