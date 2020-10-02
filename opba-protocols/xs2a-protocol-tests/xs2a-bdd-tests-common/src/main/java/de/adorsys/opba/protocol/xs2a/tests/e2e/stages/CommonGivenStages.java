package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.REGISTER_USER_ENDPOINT;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.StagesCommonUtil.SANDBOX_BANK_ID;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class CommonGivenStages<SELF extends CommonGivenStages<SELF>> extends Stage<SELF> {

    private static final String BANK_UUID_ID = SANDBOX_BANK_ID; //Define whether ais or pis

    @LocalServerPort
    private int serverPort;

    @Autowired
    private BankProfileJpaRepository profiles;

    @Autowired
    private RequestSigningService signingService;

    @ProvidedScenarioState
    protected boolean userCreated;

    @Transactional
    public SELF preferred_sca_approach_selected_for_all_banks_in_opba(Approach expectedApproach) {
        profiles.findAll().stream()
            .map(it -> {
                it.setPreferredApproach(expectedApproach);
                return it;
            })
            .forEach(profiles::save);

        return self();
    }

    @Transactional
    public SELF set_tpp_redirect_preferred_true() {
        profiles.findByBankUuid(BANK_UUID_ID)
                .map(it -> {
                    it.setPreferredApproach(Approach.REDIRECT);
                    it.setTryToUsePreferredApproach(true);
                    return it;
                })
                .ifPresent(profiles::save);

        return self();
    }

    @Transactional
    public SELF set_tpp_redirect_preferred_false() {
        profiles.findByBankUuid(BANK_UUID_ID)
                .map(it -> {
                    it.setPreferredApproach(Approach.EMBEDDED);
                    it.setTryToUsePreferredApproach(true);
                    return it;
                })
                .ifPresent(profiles::save);

        return self();
    }

    @Transactional
    public SELF set_default_preferred_approach() {
        profiles.findByBankUuid(BANK_UUID_ID)
                .map(it -> {
                    it.setPreferredApproach(Approach.REDIRECT);
                    it.setTryToUsePreferredApproach(false);
                    return it;
                })
                .ifPresent(profiles::save);

        return self();
    }

    public SELF rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api() {
        return rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api("http://localhost:" + serverPort);
    }

    public SELF rest_assured_points_to_opba_server_with_fintech_signer_on_banking_api(String opbaServerUri) {
        RestAssured.baseURI = opbaServerUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));
        RestAssured.replaceFiltersWith(new RequestSigner(signingService, new OpenBankingDataToSignProvider()));

        return self();
    }

    public SELF user_registered_in_opba_with_credentials(String username, String password) {
        if (!userCreated) {
            RestAssured
                .given()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
                .when()
                    .post(REGISTER_USER_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.CREATED.value());
            userCreated = true;
        }
        return self();
    }

    @RequiredArgsConstructor
    public static class RequestSigner implements Filter {

        private final RequestSigningService signingService;
        private final DataToSignProvider dataToSignProvider;

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            // Create signature only for FinTech-originating requests
            if (!requestSpec.getDerivedPath().startsWith("/v1/banking/ais")
                    && !requestSpec.getDerivedPath().startsWith("/v1/banking/pis")
                    && !requestSpec.getDerivedPath().startsWith("/v1/banking/search/bank-search")
                    && !requestSpec.getDerivedPath().startsWith("/v1/banking/consents/")
                    && !requestSpec.getDerivedPath().startsWith("/v1/banking/payments/")
            ) {
                return ctx.next(requestSpec, responseSpec);
            }

            Map<String, String> headers = requestSpec.getHeaders().asList().stream()
                    .collect(Collectors.toMap(Header::getName, Header::getValue, (old, newer) -> newer, HashMap::new));
            RequestToSign toSign = RequestToSign.builder()
                    .method(DataToSignProvider.HttpMethod.valueOf(requestSpec.getMethod()))
                    .path(requestSpec.getDerivedPath())
                    .headers(headers)
                    .queryParams(requestSpec.getQueryParams())
                    .body(requestSpec.getBody())
                    .build();

            String signature = dataToSignProvider.normalizerFor(toSign).canonicalStringToSign(toSign);
            requestSpec = requestSpec.replaceHeader(X_REQUEST_SIGNATURE, signingService.signature(signature));

            return ctx.next(requestSpec, responseSpec);
        }
    }
}
