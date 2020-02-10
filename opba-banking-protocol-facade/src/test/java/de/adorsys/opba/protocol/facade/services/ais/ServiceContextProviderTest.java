package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceContextProviderTest.TestConfig.class)
public class ServiceContextProviderTest {

    @Autowired
    ServiceContextProvider serviceContextProvider;
    @Autowired
    ServiceSessionRepository serviceSessionRepository;

    @Test
    void saveSessionTest() {
        UUID id = UUID.randomUUID();
        String testBankID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankID(testBankID)
                                .xRequestID(id)
                                .sessionPassword("password")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);

        assertThat(serviceSessionRepository.count()).isEqualTo(1L);
        Optional<ServiceSession> savedSession = serviceSessionRepository.findById(id);
        assertThat(savedSession).isPresent();

        byte[] encodedPassword = savedSession.get().getPassword();
        String encodedContext = savedSession.get().getContext();

        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .xRequestID(id)
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);

        assertThat(providedContext2.getRequest().getFacadeServiceable().getBankID()).isEqualTo(testBankID);
    }

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}
