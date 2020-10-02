package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.consentapi.model.generated.AuthViolation;
import de.adorsys.opba.consentapi.model.generated.ChallengeData;
import de.adorsys.opba.consentapi.model.generated.ConsentAuth;
import de.adorsys.opba.consentapi.model.generated.ScaUserData;
import de.adorsys.opba.protocol.facade.config.auth.UriExpandConst;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
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
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.AUTHORIZE_CONSENT_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.GET_CONSENT_AUTH_STATE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PASSWORD;
import static de.adorsys.opba.restapi.shared.HttpHeaders.REDIRECT_CODE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.SERVICE_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public abstract class RequestCommon<SELF extends RequestCommon<SELF>> extends Stage<SELF> {
    private static final String TPP_SERVER_PASSWORD_PLACEHOLDER = "%password%";

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
               .registerModule(new JavaTimeModule())
               .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static final String REDIRECT_CODE_QUERY = "redirectCode";

    @ProvidedScenarioState
    protected String authSessionCookie;

    @ProvidedScenarioState
    protected String redirectUriToGetUserParams;

    @ProvidedScenarioState
    protected String serviceSessionId;

    @Getter
    @ProvidedScenarioState
    protected String redirectCode;

    @ScenarioState
    protected List<ScaUserData> availableScas;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String responseContent;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String redirectOkUri;

    @ProvidedScenarioState
    @SuppressWarnings("PMD.UnusedPrivateField") // used by AccountListResult!
    protected String redirectNotOkUri;

    @ProvidedScenarioState
    protected String paymentServiceSessionId;

    @ScenarioState
    protected List<AuthViolation> violations;

    @Autowired
    protected CookieProperties cookieProperties;

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

    protected ExtractableResponse<Response> user_provides_sca_challenge_result() {
        return provideParametersToBankingProtocolWithBody(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/new-user-sca-challenge-result.json"),
                HttpStatus.ACCEPTED
        );
    }

    protected abstract ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status);

    protected ExtractableResponse<Response> provideParametersToBankingProtocolWithBody(String uriPath, String body, HttpStatus status, String serviceSessionId) {
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

    protected ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resourceData, HttpStatus status) {
        return provideParametersToBankingProtocolWithBody(uriPath, resourceData, status);
    }

    protected abstract ExtractableResponse<Response> startInitialInternalConsentAuthorization(String uriPath, String resourceData);

    protected void startInitialInternalConsentAuthorizationWithCookieValidation(String uriPath, String resourceData) {
        ExtractableResponse<Response> response =
                startInitialInternalConsentAuthorization(uriPath, resourceData, HttpStatus.ACCEPTED);
        Cookie detailedCookie = response.response().getDetailedCookie(AUTHORIZATION_SESSION_KEY);

        assertThat(detailedCookie.getMaxAge()).isEqualTo(cookieProperties.getRedirectMaxAge().getSeconds());
        assertThat(detailedCookie.getPath()).isEqualTo(getRedirectPath(serviceSessionId, redirectCode));

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

    protected ExtractableResponse<Response> max_musterman_provides_password() {
        return startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/max-musterman-password.json")
        );
    }

    protected ExtractableResponse<Response> user_provides_password(String password) {
        return startInitialInternalConsentAuthorization(
                AUTHORIZE_CONSENT_ENDPOINT,
                readResource("restrecord/tpp-ui-input/params/new-user-password.json").replace(TPP_SERVER_PASSWORD_PLACEHOLDER, password)
        );
    }

    @SneakyThrows
    protected void updateAvailableScas() {
        ExtractableResponse<Response> response = provideGetConsentAuthStateRequest();
        ConsentAuth parsedValue = JSON_MAPPER
                .readValue(response.body().asString(), ConsentAuth.class);

        this.availableScas = parsedValue.getScaMethods();
        updateRedirectCode(response);
    }

    @SneakyThrows
    protected void readViolations() {
        ExtractableResponse<Response> response = provideGetConsentAuthStateRequest();
        ConsentAuth parsedValue = JSON_MAPPER
                .readValue(response.body().asString(), ConsentAuth.class);

        this.violations = parsedValue.getViolations();
        updateRedirectCode(response);
    }

    protected abstract ExtractableResponse<Response> provideGetConsentAuthStateRequest();

    protected ExtractableResponse<Response> provideGetConsentAuthStateRequest(String serviceSessionId) {
        return RestAssured
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
    }

    protected String selectedScaBody(String scaName) {
        return String.format(
                "{\"scaAuthenticationData\":{\"SCA_CHALLENGE_ID\":\"%s\"}}",
                this.availableScas.stream().filter(it -> it.getMethodValue().equals(scaName)).findFirst().get().getId()
        );
    }

    private String getRedirectPath(String authorizationId, String redirectState) {
        return UriComponentsBuilder.fromPath(cookieProperties.getRedirectPathTemplate())
                       .buildAndExpand(ImmutableMap.of(UriExpandConst.AUTHORIZATION_SESSION_ID, authorizationId,
                                                       UriExpandConst.REDIRECT_STATE, redirectState))
                       .toUriString();
    }

    @SneakyThrows
    protected void assertThatResponseContainsCorrectChallengeData(ExtractableResponse<Response> response, String fileName) {
        ChallengeData expected = JSON_MAPPER.readValue(readResource(fileName), ChallengeData.class);

        ConsentAuth actualResponse = JSON_MAPPER.readValue(response.body().asString(), ConsentAuth.class);
        ChallengeData actual = actualResponse.getChallengeData();

        assertThat(actualResponse).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual.getImage()).isEqualTo(expected.getImage());
        assertThat(actual.getData()).isEqualTo(expected.getData());
        assertThat(actual.getImageLink()).isEqualTo(expected.getImageLink());
        assertThat(actual.getOtpMaxLength()).isEqualTo(expected.getOtpMaxLength());
        assertThat(actual.getOtpFormat()).isEqualTo(expected.getOtpFormat());
        assertThat(actual.getAdditionalInformation()).isEqualTo(expected.getAdditionalInformation());
    }
}
