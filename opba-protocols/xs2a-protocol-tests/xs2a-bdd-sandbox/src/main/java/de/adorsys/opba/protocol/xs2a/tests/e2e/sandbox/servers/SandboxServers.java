package de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.CommonGivenStages;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class SandboxServers<SELF extends SandboxServers<SELF>> extends CommonGivenStages<SELF> {

    private static final String ASPSP_PROFILE_BASE_URI = "http://localhost:20010";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SCA_REDIRECT_FLOW = "scaRedirectFlow";

    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().redirect(RedirectConfig.redirectConfig().followRedirects(false));
    }

    public SELF enabled_embedded_sandbox_mode() {
        return enabled_embedded_sandbox_mode(ASPSP_PROFILE_BASE_URI);
    }

    public SELF enabled_redirect_sandbox_mode() {
        enabled_redirect_sandbox_mode(ASPSP_PROFILE_BASE_URI);
        updateScaRedirectFlow(ASPSP_PROFILE_BASE_URI, "REDIRECT");
        return self();
    }

    public SELF enabled_oauth2_pre_step_sandbox_mode() {
        enabled_redirect_sandbox_mode(ASPSP_PROFILE_BASE_URI);
        updateScaRedirectFlow(ASPSP_PROFILE_BASE_URI, "OAUTH_PRE_STEP");
        return self();
    }

    public SELF enabled_embedded_sandbox_mode(String aspspProfileUri) {
        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("[\"EMBEDDED\",\"REDIRECT\",\"DECOUPLED\"]")
                .when()
                    .put(aspspProfileUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());

        return self();
    }

    public SELF enabled_redirect_sandbox_mode(String aspspProfileUri) {
        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("[\"REDIRECT\",\"EMBEDDED\",\"DECOUPLED\"]")
                .when()
                    .put(aspspProfileUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());

        return self();
    }

    @SneakyThrows
    private void updateScaRedirectFlow(String aspspProfileUri, String scaRedirectFlowValue) {
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(aspspProfileUri + "/api/v1/aspsp-profile")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();


        JsonNode tree = MAPPER.readTree(response.body().asString());
        ObjectNode commonEntryNode = (ObjectNode) tree.get("common");
        assertThat(commonEntryNode.get(SCA_REDIRECT_FLOW).asText()).isNotBlank();
        commonEntryNode.put(SCA_REDIRECT_FLOW, scaRedirectFlowValue);
        StringWriter writer = new StringWriter();
        MAPPER.writeTree(MAPPER.getFactory().createGenerator(writer), tree);

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(writer.toString())
                .when()
                    .put(aspspProfileUri + "/api/v1/aspsp-profile/for-debug/aspsp-settings")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }
}
