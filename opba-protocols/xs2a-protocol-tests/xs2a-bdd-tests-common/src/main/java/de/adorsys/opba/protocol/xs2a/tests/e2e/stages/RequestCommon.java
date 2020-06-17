package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.consentapi.model.generated.ScaUserData;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_XSRF_TOKEN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.ResourceUtil.readResource;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.GET_CONSENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PASSWORD;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class RequestCommon<SELF extends RequestCommon<SELF>> extends Stage<SELF> {

    public static final String REDIRECT_CODE_QUERY = "redirectCode";

    @ProvidedScenarioState
    protected String authSessionCookie;

    @ProvidedScenarioState
    protected String redirectUriToGetUserParams;

    @ProvidedScenarioState
    protected String serviceSessionId;

    @ProvidedScenarioState
    protected String redirectCode;

    @ScenarioState
    protected List<ScaUserData> availableScas;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String responseContent;

    @Autowired
    protected RequestSigningService requestSigningService;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String redirectOkUri;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String redirectNotOkUri;

    public SELF user_logged_in_into_opba_as_opba_user_with_credentials_using_fintech_supplied_url(String username, String password, String path) {
        String fintechUserTempPassword = UriComponentsBuilder
                .fromHttpUrl(redirectUriToGetUserParams).build()
                .getQueryParams()
                .getFirst(REDIRECT_CODE_QUERY);

        ExtractableResponse<Response> response =  RestAssured
                .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam(REDIRECT_CODE_QUERY, fintechUserTempPassword)
                .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
                .when()
                .post(path, serviceSessionId)
                .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .extract();

        this.authSessionCookie = response.cookie(AUTHORIZATION_SESSION_KEY);
        return self();
    }

    protected ExtractableResponse<Response> max_musterman_provides_sca_challenge_result() {
        return provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-sca-challenge-result.json"),
                HttpStatus.ACCEPTED
        );
    }

    protected ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post(uriPath, serviceSessionId)
                .then()
                .statusCode(status.value())
                .extract();

        this.responseContent = response.body().asString();
        this.redirectUriToGetUserParams = response.header(LOCATION);
        updateRedirectCode(response);
        return response;
    }

    protected ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resource, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, readResource(resource), status);
    }

    protected void startInitialInternalConsentAuthorization(String uriPath, String resource) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resource, HttpStatus.ACCEPTED);
        updateServiceSessionId(response);
        updateRedirectCode(response);
    }

    protected void updateNextConsentAuthorizationUrl(ExtractableResponse<Response> response) {
        this.redirectUriToGetUserParams = response.header(LOCATION);
    }

    protected void updateServiceSessionId(ExtractableResponse<Response> response) {
        this.serviceSessionId = response.header(SERVICE_SESSION_ID);
    }

    protected void updateRedirectCode(ExtractableResponse<Response> response) {
        this.redirectCode = response.header(REDIRECT_CODE);
    }

    protected void max_musterman_provides_password() {
        startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                "restrecord/tpp-ui-input/params/max-musterman-password.json"
        );
    }

    @SneakyThrows
    protected void updateAvailableScas() {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .header(X_REQUEST_ID, UUID.randomUUID().toString())
                .header(X_XSRF_TOKEN, UUID.randomUUID().toString())
                .cookie(AUTHORIZATION_SESSION_KEY, authSessionCookie)
                .queryParam(REDIRECT_CODE_QUERY, redirectCode)
                .when()
                .get(GET_CONSENT_AUTH_STATE, serviceSessionId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        InlineResponse200 parsedValue = new ObjectMapper()
                .readValue(response.body().asString(), InlineResponse200.class);

        this.availableScas = parsedValue.getConsentAuth().getScaMethods();
        updateRedirectCode(response);
    }

    protected String selectedScaBody(String scaName) {
        return String.format(
                "{\"scaAuthenticationData\":{\"SCA_CHALLENGE_ID\":\"%s\"}}",
                this.availableScas.stream().filter(it -> it.getMethodValue().equals(scaName)).findFirst().get().getId()
        );
    }
}
