package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.authorization.Xs2aUpdateAuthorization;
import lombok.SneakyThrows;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@ActiveProfiles("test, no-encryption")
@SpringBootTest(classes = ApplicationTest.class)
public class ServiceSessionAndAuthSessionNoEncryptionTest {
    private static final String PASSWORD = "password";
    private static final String TEST_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    private static final UUID REQUEST_ID = UUID.fromString("e3865c6b-70f2-4c1e-ad31-d7c2ff160858");
    private static final String REDIRECT_URL_OK = "http://google.com";
    private static final String REDIRECT_URL_NO_OK = "http://microsoft.com";
    private static final String NOOP_ALGO = "NOOP";

    @Autowired
    private ListAccountsService listAccountsService;

    @Autowired
    private UpdateAuthorizationService updateAuthorizationService;

    @Autowired
    private ServiceSessionRepository serviceSessionRepository;

    @Autowired
    private AuthenticationSessionRepository authenticationSessions;

    @MockBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @MockBean
    private Xs2aUpdateAuthorization xs2aUpdateAuthorization;

    @Test
    @SneakyThrows
    void serviceSessionAndAuthSessionWithConsent_success() {
        UUID sessionId = UUID.randomUUID();
        ValidationErrorResult validationErrorResult = buildValidationErrorResultResult();
        FacadeStartAuthorizationResult listAccountsResponse = createAndAssertListAccountRequestForBruecker(sessionId, validationErrorResult);

        await().atMost(Durations.ONE_SECOND)
                .pollDelay(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findAll().iterator().hasNext());

        assertAllSessions(sessionId);

        AuthorizationRequiredResult authorizationRequiredResult = buildAuthorizationRequiredResult();

        doAnswer(invocation -> CompletableFuture.completedFuture(authorizationRequiredResult))
                .when(xs2aUpdateAuthorization)
                .execute(any(ServiceContext.class));

        FacadeRedirectResult authUpdatedResult = (FacadeRedirectResult) updateAuthorizationService.execute(buildAuthRequest(listAccountsResponse)).get();

        assertThat(UUID.fromString(authUpdatedResult.getAuthorizationSessionId())).isEqualTo(sessionId);
        assertThat(UUID.fromString(authUpdatedResult.getServiceSessionId())).isEqualTo(sessionId);

        assertThat(authUpdatedResult.getRedirectionTo()).isEqualTo(authorizationRequiredResult.getRedirectionTo());

        assertAllSessions(sessionId);
    }

    @Test
    @SneakyThrows
    void serviceSession_protocolError() {
        UUID sessionId = UUID.randomUUID();

        ErrorResult<AuthorizationRequiredResult> errorResult = new ErrorResult<>();
        errorResult.setCode("400");
        errorResult.setMessage("The addressed resource is unknown relative to the TPP");

        doAnswer(invocation -> CompletableFuture.completedFuture(errorResult))
                .when(xs2aListAccountsEntrypoint)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) listAccountsService.execute(buildListAccountRequest(sessionId)).get();

        assertErrorResponse(errorResponse, errorResult, sessionId);

        await().atMost(Durations.ONE_SECOND)
                .pollDelay(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findAll().iterator().hasNext());

        assertAllSessions(sessionId);
    }

    @Test
    @SneakyThrows
    void authSession_protocolError() {
        UUID sessionId = UUID.randomUUID();
        ValidationErrorResult validationErrorResult = buildValidationErrorResultResult();
        FacadeStartAuthorizationResult listAccountsResponse = createAndAssertListAccountRequestForBruecker(sessionId, validationErrorResult);

        await().atMost(Durations.ONE_SECOND)
                .pollDelay(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findAll().iterator().hasNext());

        assertAllSessions(sessionId);


        ErrorResult<AuthorizationRequiredResult> errorResult = new ErrorResult<>();
        errorResult.setCode("400");
        errorResult.setMessage("The addressed resource is unknown relative to the TPP");

        doAnswer(invocation -> CompletableFuture.completedFuture(errorResult))
                .when(xs2aUpdateAuthorization)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) updateAuthorizationService.execute(buildAuthRequest(listAccountsResponse)).get();

        assertErrorResponse(errorResponse, errorResult, sessionId);
        assertAllSessions(sessionId);
    }

    @Test
    @SneakyThrows
    void serviceSession_success() {
        UUID sessionId = UUID.randomUUID();
        AuthorizationRequiredResult authorizationRequiredResult = buildAuthorizationRequiredResult();

        createAndAssertListAccountRequestForBruecker(sessionId, authorizationRequiredResult);

        await().atMost(Durations.ONE_SECOND)
                .pollDelay(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findAll().iterator().hasNext());

        assertAllSessions(sessionId);
    }

    @SneakyThrows
    private FacadeStartAuthorizationResult createAndAssertListAccountRequestForBruecker(UUID sessionId, RedirectionResult redirectionResult) {
        doAnswer(invocation -> CompletableFuture.completedFuture(redirectionResult))
                .when(xs2aListAccountsEntrypoint)
                .execute(any(ServiceContext.class));

        FacadeStartAuthorizationResult listAccountsResponse = (FacadeStartAuthorizationResult) listAccountsService.execute(buildListAccountRequest(sessionId)).get();

        assertThat(UUID.fromString(listAccountsResponse.getAuthorizationSessionId())).isEqualTo(sessionId);
        assertThat(UUID.fromString(listAccountsResponse.getServiceSessionId())).isEqualTo(sessionId);
        assertThat(listAccountsResponse.getRedirectionTo()).isEqualTo(redirectionResult.getRedirectionTo());
        return listAccountsResponse;
    }

    private AuthorizationRequest buildAuthRequest(FacadeStartAuthorizationResult listAccountsResponse) {
        Map<String, String> authData = new HashMap<>();
        authData.put("consent.frequencyPerDay", "5");
        authData.put("psuIpAddress", "1.1.1.1"); // NOPMD Hard code the IP address in test is not a problem
        authData.put("psuId", "anton.brueckner");
        authData.put("consent.validUntil", "2020-02-26T22:00:00.000Z");
        authData.put("consent.access.allAccountsAccess", "allAccountsWithBalances");

        return AuthorizationRequest.builder()
                       .facadeServiceable(FacadeServiceableRequest.builder()
                                                  .redirectCode(listAccountsResponse.getRedirectCode())
                                                  .authorizationSessionId(listAccountsResponse.getAuthorizationSessionId())
                                                  .requestId(listAccountsResponse.getXRequestId())
                                                  .build()
                       )
                       .scaAuthenticationData(authData)
                       .build();
    }

    private ListAccountsRequest buildListAccountRequest(UUID sessionId) {
        return ListAccountsRequest.builder()
                       .facadeServiceable(
                               FacadeServiceableRequest.builder()
                                       .bankId(TEST_BANK_ID)
                                       .requestId(REQUEST_ID)
                                       .serviceSessionId(sessionId)
                                       .sessionPassword(PASSWORD)
                                       .fintechRedirectUrlOk(REDIRECT_URL_OK)
                                       .fintechRedirectUrlNok(REDIRECT_URL_NO_OK)
                                       .build()
                       ).build();
    }

    private void assertAllSessions(UUID sessionId) {
        Optional<AuthSession> authenticationSessionOptional = authenticationSessions.findById(sessionId);
        Optional<ServiceSession> serviceSessionOptional = serviceSessionRepository.findById(sessionId);

        assertThat(authenticationSessionOptional.isPresent()).isTrue();
        assertThat(serviceSessionOptional.isPresent()).isTrue();

        AuthSession authenticationSession = authenticationSessionOptional.get();
        ServiceSession serviceSessionFromAuth = authenticationSession.getParent();
        ServiceSession serviceSessionFromDB = serviceSessionOptional.get();

        assertThat(serviceSessionFromDB.getId()).isEqualTo(serviceSessionFromAuth.getId());
        assertThat(serviceSessionFromDB.getAuthSession().getId()).isEqualTo(authenticationSession.getId());
        assertThat(serviceSessionFromDB.getAuthSession().getRedirectCode()).isEqualTo(authenticationSession.getRedirectCode());
        assertThat(serviceSessionFromDB.getAlgo()).isEqualTo(NOOP_ALGO);

    }

    private void assertErrorResponse(FacadeRedirectErrorResult errorResponse, ErrorResult<AuthorizationRequiredResult> errorResult, UUID sessionId) {
        assertThat(UUID.fromString(errorResponse.getAuthorizationSessionId())).isEqualTo(sessionId);
        assertThat(UUID.fromString(errorResponse.getServiceSessionId())).isEqualTo(sessionId);
        assertThat(errorResponse.getXRequestId()).isEqualTo(REQUEST_ID);
        assertThat(errorResponse.getRedirectionTo().toString()).isEqualTo(REDIRECT_URL_NO_OK);
        assertThat(errorResponse.getHeaders().get(ResponseHeaders.X_ERROR_CODE)).isEqualTo(errorResult.getCode());
        assertThat(errorResponse.getHeaders().get(ResponseHeaders.X_ERROR_MESSAGE)).isEqualTo(errorResult.getMessage());
    }

    private AuthorizationRequiredResult buildAuthorizationRequiredResult() {
        return new AuthorizationRequiredResult(URI.create("http://localhost:4400/account-information/login?encryptedConsentId=ZwaFosuf-GopQSJwEcWKIn708zVNbrHvmWZh6amuM59aBEMABLWcMZATy6v9CghY5hpqT3HlUQAcRciH8hB3asz9MpaJIQIH3NJX8IHgetw=_=_psGLvQpt9Q&redirectId=7154aa62-b744-4072-b4b9-0fa87048e2c8"));
    }

    private ValidationErrorResult buildValidationErrorResultResult() {
        return new ValidationErrorResult(URI.create("http://localhost:5500/parameters/provide-more/8bce1a14-5a43-11ea-893e-acde48001122?q=%5B%7B%22uiCode%22:%22textbox.integer%22,%20%22ctxCode%22:%22frequencyPerDay%22,%20%22message%22:%22%7Bno.ctx.frequencyPerDay%7D%22,%20%22target%22:%22AIS_CONSENT%22%7D,%20%7B%22uiCode%22:%22textbox.string%22,%20%22ctxCode%22:%22PSU_ID%22,%20%22message%22:%22PSU%20id%20(username%20to%20login%20to%20bank%20account)%22,%20%22target%22:%22CONTEXT%22%7D,%20%7B%22uiCode%22:%22textbox.string%22,%20%22ctxCode%22:%22PSU_IP_ADDRESS%22,%20%22message%22:%22%7Bno.ctx.psuIpAddress%7D%22,%20%22target%22:%22CONTEXT%22%7D,%20%7B%22uiCode%22:%22accountaccess.class%22,%20%22ctxCode%22:%22access%22,%20%22message%22:%22%7Bno.ctx.accountaccess%7D%22,%20%22target%22:%22AIS_CONSENT%22%7D,%20%7B%22uiCode%22:%22boolean.boolean%22,%20%22ctxCode%22:%22recurringIndicator%22,%20%22message%22:%22%7Bno.ctx.recurringIndicator%7D%22,%20%22target%22:%22AIS_CONSENT%22%7D,%20%7B%22uiCode%22:%22date.string%22,%20%22ctxCode%22:%22validUntil%22,%20%22message%22:%22%7Bno.ctx.validUntil%7D%22,%20%22target%22:%22AIS_CONSENT%22%7D%5D"));
    }
}