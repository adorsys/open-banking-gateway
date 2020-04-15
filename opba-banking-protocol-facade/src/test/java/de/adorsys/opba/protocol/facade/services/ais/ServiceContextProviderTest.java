package de.adorsys.opba.protocol.facade.services.ais;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.URI;
import java.util.UUID;

import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@SuppressWarnings({"PMD.UnusedLocalVariable", "PMD.UnusedFormalParameter"}) // FIXME https://github.com/adorsys/open-banking-gateway/issues/557
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
public class ServiceContextProviderTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    private static final String PASSWORD = "password";
    private static final String PROTOCOL_DEFINED_DATA_TO_STORE_IN_CONTEXT = "some context data";

    @Autowired
    private ProtocolResultHandler handler;

    @Autowired
    @Qualifier(FINTECH_CONTEXT_PROVIDER)
    private ServiceContextProvider serviceContextProvider;

    @Autowired
    private ServiceSessionRepository serviceSessionRepository;

    @Autowired
    private BankProtocolRepository protocolRepository;


    @Autowired
    private TransactionTemplate txTemplate;

    @Test
    @SneakyThrows
    void saveSessionTest() {
        UUID sessionId = UUID.randomUUID();
        String testBankID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
        ListAccountsRequest request = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .bankId(testBankID)
                                .requestId(UUID.randomUUID())
                                .serviceSessionId(sessionId)
                                .sessionPassword(PASSWORD)
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);
        EncryptionService encryptionService = providedContext.getRequestScoped().encryption();
        URI redirectionTo = new URI("/");
        Result<URI> result = new ConsentAcquiredResult<>(redirectionTo, null);
        FacadeRedirectResult<URI, AuthStateBody> uriFacadeResult = (FacadeRedirectResult)
            handler.handleResult(result, request.getFacadeServiceable(), providedContext);

        assertThat(providedContext.getRequest().getFacadeServiceable().getSessionPassword()).isEqualTo(PASSWORD);

        txTemplate.execute(callback -> {
            checkSavedSession(sessionId, encryptionService, request.getFacadeServiceable());
            return null;
        });


        // check that stored data is encrypted
        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .serviceSessionId(sessionId)
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .authorizationSessionId(uriFacadeResult.getAuthorizationSessionId())
                                .redirectCode(uriFacadeResult.getRedirectCode())
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);
        EncryptionService encryptionService2 = providedContext2.getRequestScoped().encryption();

        txTemplate.execute(callback -> {
            secondRequestCheck(sessionId, encryptionService2);
            return null;
        });
    }

    // FIXME
    @SneakyThrows
    private void checkSavedSession(UUID sessionId, EncryptionService encryptionService, FacadeServiceableRequest facadeServiceable) {
        ServiceSession session = serviceSessionRepository.findById(sessionId).get();

        // check that in context stored first request parameters facadServicable
        // storing some data to context using provided encryption service
        String encryptedContext = new String(encryptionService.encrypt(
            MAPPER.writeValueAsBytes(PROTOCOL_DEFINED_DATA_TO_STORE_IN_CONTEXT))
        );
        session.setProtocol(protocolRepository.findAll().iterator().next());
        serviceSessionRepository.save(session);
    }

    // FIXME
    @SneakyThrows
    private void secondRequestCheck(UUID sessionId, EncryptionService encryptionService2) {
        ServiceSession sessionForCheck = serviceSessionRepository.findById(sessionId).orElseThrow(
                () -> new IllegalArgumentException("Session not found:" + sessionId)
        );
    }
}
