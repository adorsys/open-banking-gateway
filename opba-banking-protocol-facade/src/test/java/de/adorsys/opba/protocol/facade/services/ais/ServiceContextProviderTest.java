package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceContextProviderTest.TestConfig.class)
public class ServiceContextProviderTest {

    @Autowired
    ServiceContextProvider serviceContextProvider;

    @Test
    void saveSessionTest() {
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankID("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46")
                                .xRequestID(UUID.randomUUID())
                                .sessionPassword("password")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> context = serviceContextProvider.provide(request);


    }

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}
