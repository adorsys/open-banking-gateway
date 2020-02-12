package de.adorsys.opba.protocol.facade.services.ais;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceContextProviderTest.TestConfig.class)
public class ServiceContextProviderTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @Autowired
    ServiceContextProvider serviceContextProvider;

    @Autowired
    ServiceSessionRepository serviceSessionRepository;

    @Autowired
    EncryptionService encryptionService;

    @Test
    @SneakyThrows
    void saveSessionTest() {
        UUID id = UUID.randomUUID();
        String testBankID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
        String password = "password";
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankID(testBankID)
                                .xRequestID(id)
                                .sessionPassword(password)
                                .fintechRedirectURLOK("http://google.com")
                                .fintechRedirectURLNOK("http://microsoft.com")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);
        assertThat(providedContext.getBankId()).isEqualTo(testBankID);
        assertThat(providedContext.getRequest().getFacadeServiceable().getSessionPassword()).isEqualTo(password);

        assertThat(serviceSessionRepository.count()).isEqualTo(1L);
        Optional<ServiceSession> savedSession = serviceSessionRepository.findById(id);
        assertThat(savedSession).isPresent();

        SecretKey key = encryptionService.deriveKey(password);
        assertThat(encryptionService.decryptSecretKey(savedSession.get().getSecretKey())).isEqualTo(key.getEncoded());
        byte[] decryptedData = encryptionService.decrypt(savedSession.get().getContext().getBytes(), key.getEncoded());
        assertThat(decryptedData).isEqualTo(MAPPER.writeValueAsBytes(request.getFacadeServiceable()));

        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .xRequestID(id)
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);

        assertThat(providedContext2.getBankId()).isEqualTo(testBankID);
    }

    @EnableXs2aProtocol
    @EnableBankingPersistence
    @SpringBootApplication(scanBasePackages = "de.adorsys.opba.protocol.facade")
    public static class TestConfig {
    }
}
