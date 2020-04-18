package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.AuthorizationRequiredResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog.ValidationErrorResult;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.error.ErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectErrorResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeRedirectResult;
import de.adorsys.opba.protocol.facade.dto.result.torest.redirectable.FacadeStartAuthorizationResult;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.xs2a.entrypoint.ais.Xs2aListAccountsEntrypoint;
import de.adorsys.opba.protocol.xs2a.entrypoint.authorization.Xs2aUpdateAuthorization;
import liquibase.integration.spring.SpringLiquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_CODE;
import static de.adorsys.opba.protocol.api.dto.headers.ResponseHeaders.X_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;
import static org.awaitility.Durations.ONE_SECOND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

abstract class AbstractServiceSessionTest {
    private static final String FINTECH_ID = "MY-SUPER-FINTECH";
    private static final String FINTECH_USER_ID = "user@fintech.com";
    private static final String PASSWORD = "password";
    private static final String TEST_BANK_ID = "53c47f54-b9a4-465a-8f77-bc6cd5f0cf46";
    private static final UUID REQUEST_ID = UUID.fromString("e3865c6b-70f2-4c1e-ad31-d7c2ff160858");
    private static final String REDIRECT_URL_OK = "http://google.com";
    private static final String REDIRECT_URL_NO_OK = "http://microsoft.com";
    private static final ErrorResult<AuthorizationRequiredResult> ERROR_RESULT = buildErrorResult();
    private static final String ERROR_CODE_400 = "400";
    private static final String ERROR_RESULT_MESSAGE = "The addressed resource is unknown relative to the TPP";
    private static final UUID SESSION_ID = UUID.randomUUID();

    @Autowired
    private ListAccountsService listAccountsService;

    @Autowired
    private UpdateAuthorizationService updateAuthorizationService;

    @Autowired
    private ServiceSessionRepository serviceSessionRepository;

    @Autowired
    private AuthorizationSessionRepository authenticationSessions;

    @Autowired
    private SpringLiquibase liquibase;

    @MockBean
    private Xs2aListAccountsEntrypoint xs2aListAccountsEntrypoint;

    @MockBean
    private Xs2aUpdateAuthorization xs2aUpdateAuthorization;

    @AfterEach
    @SneakyThrows
    void setup() {
        // drop (drop-first: true) and re-create DB
        liquibase.afterPropertiesSet();
    }

    @Test
    @SneakyThrows
    void serviceSessionAndAuthSessionWithConsent_success() {
        ValidationErrorResult validationErrorResult = buildValidationErrorResultResult();
        FacadeStartAuthorizationResult listAccountsResponse = createAndAssertListAccountRequestForBruecker(validationErrorResult);

        assertThat(listAccountsResponse.getAuthorizationSessionId()).isEqualTo(SESSION_ID.toString());

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(SESSION_ID).isPresent());

        assertServiceAndAuthorizationSessions();

        AuthorizationRequiredResult authorizationRequiredResult = buildAuthorizationRequiredResult();

        doAnswer(invocation -> CompletableFuture.completedFuture(authorizationRequiredResult))
                .when(xs2aUpdateAuthorization)
                .execute(any(ServiceContext.class));

        FacadeRedirectResult authUpdatedResult = (FacadeRedirectResult) updateAuthorizationService.execute(buildAuthRequest(listAccountsResponse)).get();

        assertThat(authUpdatedResult.getAuthorizationSessionId()).isEqualTo(SESSION_ID.toString());
        assertThat(authUpdatedResult.getServiceSessionId()).isEqualTo(SESSION_ID.toString());

        assertThat(authUpdatedResult.getRedirectionTo()).isEqualTo(authorizationRequiredResult.getRedirectionTo());

        assertServiceAndAuthorizationSessions();
    }

    @Test
    @SneakyThrows
    void serviceSession_protocolError() {
        doAnswer(invocation -> CompletableFuture.completedFuture(ERROR_RESULT))
                .when(xs2aListAccountsEntrypoint)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) listAccountsService.execute(buildListAccountRequest()).get();

        assertErrorResponse(errorResponse, SESSION_ID.toString(), false);
    }

    @Test
    @SneakyThrows
    void authSession_protocolError() {
        ValidationErrorResult validationErrorResult = buildValidationErrorResultResult();
        FacadeStartAuthorizationResult listAccountsResponse = createAndAssertListAccountRequestForBruecker(validationErrorResult);

        assertThat(listAccountsResponse.getAuthorizationSessionId()).isEqualTo(SESSION_ID.toString());

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(SESSION_ID).isPresent());

        assertServiceAndAuthorizationSessions();

        doAnswer(invocation -> CompletableFuture.completedFuture(ERROR_RESULT))
                .when(xs2aUpdateAuthorization)
                .execute(any(ServiceContext.class));

        FacadeRedirectErrorResult errorResponse = (FacadeRedirectErrorResult) updateAuthorizationService.execute(buildAuthRequest(listAccountsResponse)).get();

        assertErrorResponse(errorResponse, SESSION_ID.toString(), true);
        assertServiceAndAuthorizationSessions();
    }

    @Test
    @SneakyThrows
    void serviceSession_success() {
        AuthorizationRequiredResult authorizationRequiredResult = buildAuthorizationRequiredResult();

        createAndAssertListAccountRequestForBruecker(authorizationRequiredResult);

        await().atMost(ONE_SECOND)
                .pollDelay(ONE_HUNDRED_MILLISECONDS)
                .until(() -> authenticationSessions.findById(SESSION_ID).isPresent());

        assertServiceAndAuthorizationSessions();
    }

    @SneakyThrows
    private FacadeStartAuthorizationResult createAndAssertListAccountRequestForBruecker(RedirectionResult redirectionResult) {
        doAnswer(invocation -> CompletableFuture.completedFuture(redirectionResult))
                .when(xs2aListAccountsEntrypoint)
                .execute(any(ServiceContext.class));

        FacadeStartAuthorizationResult listAccountsResponse = (FacadeStartAuthorizationResult) listAccountsService.execute(buildListAccountRequest()).get();

        assertThat(listAccountsResponse.getAuthorizationSessionId()).isEqualTo(SESSION_ID.toString());
        assertThat(listAccountsResponse.getServiceSessionId()).isEqualTo(SESSION_ID.toString());
        assertThat(listAccountsResponse.getRedirectionTo()).asString().contains("localhost:1010").contains("ais").contains("login?redirectCode=");
        return listAccountsResponse;
    }

    private AuthorizationRequest buildAuthRequest(FacadeStartAuthorizationResult listAccountsResponse) {
        return AuthorizationRequest.builder()
                       .facadeServiceable(FacadeServiceableRequest.builder()
                               .authorization(FINTECH_ID)
                               .sessionPassword(PASSWORD)
                               .bankId(TEST_BANK_ID)
                               .redirectCode(listAccountsResponse.getRedirectCode())
                               .authorizationSessionId(listAccountsResponse.getAuthorizationSessionId())
                               .fintechRedirectUrlNok(REDIRECT_URL_NO_OK)
                               .requestId(listAccountsResponse.getXRequestId())
                               .build()
                       )
                       .build();
    }

    private ListAccountsRequest buildListAccountRequest() {
        return ListAccountsRequest.builder()
                       .facadeServiceable(
                               FacadeServiceableRequest.builder()
                                       .bankId(TEST_BANK_ID)
                                       .requestId(REQUEST_ID)
                                       .serviceSessionId(SESSION_ID)
                                       .sessionPassword(PASSWORD)
                                       .fintechUserId(FINTECH_USER_ID)
                                       .authorization(FINTECH_ID)
                                       .fintechRedirectUrlOk(REDIRECT_URL_OK)
                                       .fintechRedirectUrlNok(REDIRECT_URL_NO_OK)
                                       .build()
                       ).build();
    }

    private void assertServiceAndAuthorizationSessions() {
        AuthSession authenticationSession = authenticationSessions.findById(SESSION_ID).get();
        ServiceSession serviceSessionFromDB = serviceSessionRepository.findById(SESSION_ID).get();

        ServiceSession serviceSessionFromAuth = authenticationSession.getParent();

        assertThat(serviceSessionFromDB.getId()).isEqualTo(serviceSessionFromAuth.getId());
        assertThat(serviceSessionFromDB.getAuthSession().getId()).isEqualTo(authenticationSession.getId());
        assertThat(serviceSessionFromDB.getAuthSession().getRedirectCode()).isEqualTo(authenticationSession.getRedirectCode());
    }

    private void assertErrorResponse(FacadeRedirectErrorResult errorResponse, String sessionId, boolean authSessionIsOpen) {
        if (!authSessionIsOpen) {
            assertThat(errorResponse.getAuthorizationSessionId()).isNull();
        } else {
            assertThat(errorResponse.getAuthorizationSessionId()).isEqualTo(sessionId);
        }

        assertThat(errorResponse.getServiceSessionId()).isEqualTo(sessionId);
        assertThat(errorResponse.getXRequestId()).isEqualTo(REQUEST_ID);
        assertThat(errorResponse.getRedirectionTo().toString()).isEqualTo(REDIRECT_URL_NO_OK);
        assertThat(errorResponse.getHeaders().get(X_ERROR_CODE)).isEqualTo(ERROR_CODE_400);
        assertThat(errorResponse.getHeaders().get(X_ERROR_MESSAGE)).isEqualTo(ERROR_RESULT_MESSAGE);
    }

    private AuthorizationRequiredResult buildAuthorizationRequiredResult() {
        return new AuthorizationRequiredResult(
            URI.create("http://localhost:4400/account-information"),
            null
        );
    }

    private ValidationErrorResult buildValidationErrorResultResult() {
        return new ValidationErrorResult(
            URI.create("http://localhost:5500/parameters/provide-more/8bce1a14-5a43-11ea-893e-acde48001122"),
            null
        );
    }

    private static ErrorResult<AuthorizationRequiredResult> buildErrorResult() {
        ErrorResult<AuthorizationRequiredResult> errorResult = new ErrorResult<>();
        errorResult.setCode(ERROR_CODE_400);
        errorResult.setMessage(ERROR_RESULT_MESSAGE);

        return errorResult;
    }
}
