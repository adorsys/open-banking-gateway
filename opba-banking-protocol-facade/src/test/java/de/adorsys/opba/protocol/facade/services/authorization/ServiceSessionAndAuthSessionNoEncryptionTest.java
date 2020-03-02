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

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Note: This test keeps DB in dirty state - doesn't cleanup after itself.
 */
@ActiveProfiles("test, no-encryption")
@SpringBootTest(classes = ApplicationTest.class)
class ServiceSessionAndAuthSessionNoEncryptionTest {
    private static final String PASSWORD = "password";
    private static final String TEST_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    private static final UUID REQUEST_ID = UUID.fromString("e3865c6b-70f2-4c1e-ad31-d7c2ff160858");
    private static final String REDIRECT_URL_OK = "http://google.com";
    private static final String REDIRECT_URL_NO_OK = "http://microsoft.com";
    private static final String NOOP_ALGO = "NOOP";
    private static final ErrorResult<AuthorizationRequiredResult> ERROR_RESULT = buildErrorResult();

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

        assertThat(listAccountsResponse.getAuthorizationSessionId()).isEqualTo(sessionId.toString());

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(sessionId).isPresent());

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

        doAnswer(invocation -> CompletableFuture.completedFuture(ERROR_RESULT))
                .when(xs2aListAccountsEntrypoint)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) listAccountsService.execute(buildListAccountRequest(sessionId)).get();

        assertErrorResponse(errorResponse, ERROR_RESULT, sessionId);

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(sessionId).isPresent());

        assertAllSessions(sessionId);
    }

    @Test
    @SneakyThrows
    void authSession_protocolError() {
        UUID sessionId = UUID.randomUUID();
        ValidationErrorResult validationErrorResult = buildValidationErrorResultResult();
        FacadeStartAuthorizationResult listAccountsResponse = createAndAssertListAccountRequestForBruecker(sessionId, validationErrorResult);

        assertThat(listAccountsResponse.getAuthorizationSessionId()).isEqualTo(sessionId.toString());

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(sessionId).isPresent());

        assertAllSessions(sessionId);

        doAnswer(invocation -> CompletableFuture.completedFuture(ERROR_RESULT))
                .when(xs2aUpdateAuthorization)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) updateAuthorizationService.execute(buildAuthRequest(listAccountsResponse)).get();

        assertErrorResponse(errorResponse, ERROR_RESULT, sessionId);
        assertAllSessions(sessionId);
    }

    @Test
    @SneakyThrows
    void serviceSession_success() {
        UUID sessionId = UUID.randomUUID();
        AuthorizationRequiredResult authorizationRequiredResult = buildAuthorizationRequiredResult();

        createAndAssertListAccountRequestForBruecker(sessionId, authorizationRequiredResult);

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(sessionId).isPresent());

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
        return AuthorizationRequest.builder()
                       .facadeServiceable(FacadeServiceableRequest.builder()
                                                  .redirectCode(listAccountsResponse.getRedirectCode())
                                                  .authorizationSessionId(listAccountsResponse.getAuthorizationSessionId())
                                                  .requestId(listAccountsResponse.getXRequestId())
                                                  .build()
                       )
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
        AuthSession authenticationSession = authenticationSessions.findById(sessionId).get();
        ServiceSession serviceSessionFromDB = serviceSessionRepository.findById(sessionId).get();

        ServiceSession serviceSessionFromAuth = authenticationSession.getParent();

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
        return new AuthorizationRequiredResult(URI.create("http://localhost:4400/account-information"));
    }

    private ValidationErrorResult buildValidationErrorResultResult() {
        return new ValidationErrorResult(URI.create("http://localhost:5500/parameters/provide-more/8bce1a14-5a43-11ea-893e-acde48001122"));
    }

    private static ErrorResult<AuthorizationRequiredResult> buildErrorResult() {
        ErrorResult<AuthorizationRequiredResult> errorResult = new ErrorResult<>();
        errorResult.setCode("400");
        errorResult.setMessage("The addressed resource is unknown relative to the TPP");

        return errorResult;
    }
}