package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ConsentAcquiredResult;
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
@SuppressWarnings({"PMD.UnusedLocalVariable", "PMD.UnusedFormalParameter"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
public class ServiceContextProviderTest {

    private static final String PASSWORD = "password";

    @Autowired
    private ProtocolResultHandler handler;

    @Autowired
    @Qualifier(FINTECH_CONTEXT_PROVIDER)
    private ServiceContextProvider serviceContextProvider;

    @Autowired
    private ServiceSessionRepository serviceSessionRepository;

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
                                .authorization("SUPER-FINTECH-ID")
                                .fintechUserId("user1@fintech.com")
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .build()
                ).build();

        ServiceContext<FacadeServiceableGetter> providedContext = serviceContextProvider.provide(request);
        URI redirectionTo = new URI("/");
        Result<URI> result = new ConsentAcquiredResult<>(redirectionTo, null);
        FacadeRedirectResult<URI, AuthStateBody> uriFacadeResult = (FacadeRedirectResult)
            handler.handleResult(result, request.getFacadeServiceable(), providedContext);

        assertThat(providedContext.getRequest().getFacadeServiceable().getSessionPassword()).isEqualTo(PASSWORD);

        txTemplate.execute(callback -> {
            checkSavedSession(sessionId);
            return null;
        });


        // check that stored data is encrypted
        ListAccountsRequest request2 = ListAccountsRequest.builder()
                .facadeServiceable(
                        FacadeServiceableRequest.builder()
                                .serviceSessionId(sessionId)
                                .bankId(testBankID)
                                .sessionPassword(PASSWORD)
                                .authorization("SUPER-FINTECH-ID")
                                .fintechUserId("user1@fintech.com")
                                .fintechRedirectUrlOk("http://google.com")
                                .fintechRedirectUrlNok("http://microsoft.com")
                                .authorizationSessionId(uriFacadeResult.getAuthorizationSessionId())
                                .redirectCode(uriFacadeResult.getRedirectCode())
                                .build()
                ).build();
        ServiceContext<FacadeServiceableGetter> providedContext2 = serviceContextProvider.provide(request2);

        txTemplate.execute(callback -> {
            secondRequestCheck(sessionId);
            return null;
        });
    }

    // FIXME
    @SneakyThrows
    private void checkSavedSession(UUID sessionId) {
        assertThat(serviceSessionRepository.findById(sessionId)).isPresent();
    }

    // FIXME
    @SneakyThrows
    private void secondRequestCheck(UUID sessionId) {
        assertThat(serviceSessionRepository.findById(sessionId)).isPresent();
    }
}
