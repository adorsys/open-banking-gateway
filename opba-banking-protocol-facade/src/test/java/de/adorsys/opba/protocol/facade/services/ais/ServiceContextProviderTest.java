package de.adorsys.opba.protocol.facade.services.ais;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.KeyWithParamsDto;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.services.FacadeEncryptionServiceFactory;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
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
    SecretKeyOperations secretKeyOperations;

    @Autowired
    FacadeEncryptionServiceFactory facadeEncryptionServiceFactory;

    @Test
    @SneakyThrows
    @Transactional
    void saveSessionTest() {
        UUID id = UUID.randomUUID();
        String testBankID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
        String password = "password";
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankId(testBankID)
                                .requestId(id)
                                .sessionPassword(password)
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);
        assertThat(providedContext.getBankId()).isEqualTo(testBankID);
        assertThat(providedContext.getRequest().getFacadeServiceable().getSessionPassword()).isEqualTo(password);

        assertThat(serviceSessionRepository.count()).isEqualTo(1L);
        Iterable<ServiceSession> all = serviceSessionRepository.findAll();
        assertThat(all.iterator().hasNext()).isTrue();
        ServiceSession session = all.iterator().next();

        KeyWithParamsDto keyWithParamsDto = secretKeyOperations.generateKey(
                password,
                session.getAlgo(),
                session.getSalt(),
                session.getIterCount()
        );
        assertThat(secretKeyOperations.decrypt(session.getSecretKey())).isEqualTo(keyWithParamsDto.getKey());

        EncryptionService encryptionService = facadeEncryptionServiceFactory.provideEncryptionService(keyWithParamsDto.getKey());
        byte[] decryptedData = encryptionService.decrypt(session.getContext().getBytes());
        assertThat(decryptedData).isEqualTo(MAPPER.writeValueAsBytes(request.getFacadeServiceable()));

        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankId(testBankID)
                                .serviceSessionId(session.getId())
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);

        assertThat(providedContext2.getRequest().getFacadeServiceable().getBankId()).isEqualTo(testBankID);
    }
}
