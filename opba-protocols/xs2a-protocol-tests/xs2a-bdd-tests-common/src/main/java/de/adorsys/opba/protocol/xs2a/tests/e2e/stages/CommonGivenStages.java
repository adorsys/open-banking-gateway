package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.IgnoreBankValidationRule;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.IgnoreBankValidationRuleRepository;
import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.tests.HeaderNames.X_REQUEST_ID;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.LOGIN;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.PASSWORD;
import static de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AisStagesCommonUtil.REGISTER_USER_ENDPOINT;
import static io.restassured.RestAssured.config;
import static io.restassured.config.RedirectConfig.redirectConfig;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class CommonGivenStages<SELF extends CommonGivenStages<SELF>> extends Stage<SELF> {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private BankProfileJpaRepository profiles;

    @Autowired
    private IgnoreBankValidationRuleRepository ignoreBankValidationRuleRepository;

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

    public SELF rest_assured_points_to_opba_server() {
        return rest_assured_points_to_opba_server("http://localhost:" + serverPort);
    }

    public SELF rest_assured_points_to_opba_server(String opbaServerUri) {
        RestAssured.baseURI = opbaServerUri;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        config = config().redirect(redirectConfig().followRedirects(false));

        return self();
    }

    public SELF user_registered_in_opba_with_credentials(String username, String password) {
        RestAssured
                .given()
                    .header(X_REQUEST_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(ImmutableMap.of(LOGIN, username, PASSWORD, password))
                .when()
                    .post(REGISTER_USER_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.CREATED.value());

        return self();
    }

    public SELF ignore_validation_rules_table_contains_field_psu_id() {
        IgnoreBankValidationRule bankValidationRule = IgnoreBankValidationRule.builder()
                .protocol(BankProtocol.builder().id(1L).build())
                .endpointClassCanonicalName("de.adorsys.opba.protocol.xs2a.service.xs2a.ais.AccountListingService")
                .forEmbedded(false)
                .forRedirect(true)
                .validationCode(FieldCode.PSU_ID)
                .build();
        ignoreBankValidationRuleRepository.deleteAll();
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName("de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateAisAccountListConsentService");
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        bankValidationRule.setId(null);
        bankValidationRule.setEndpointClassCanonicalName("de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.StartConsentAuthorization");
        ignoreBankValidationRuleRepository.save(bankValidationRule);
        return self();
    }
}
