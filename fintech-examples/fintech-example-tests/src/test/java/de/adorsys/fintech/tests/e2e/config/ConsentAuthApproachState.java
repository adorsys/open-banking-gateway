package de.adorsys.fintech.tests.e2e.config;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

@RequiredArgsConstructor
public class ConsentAuthApproachState {

    private final String aspspProfileServerUri;

    private List<String> memoizedApproaches;

    public void memoize() {
        ExtractableResponse<Response> response = RestAssured
                .when()
                    .get(aspspProfileServerUri + "/api/v1/aspsp-profile/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value())
                .extract();

        this.memoizedApproaches = response.body().as(new TypeRef<>() {});
    }

    public void restore() {
        if (null == this.memoizedApproaches) {
            return;
        }

        RestAssured
                .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(memoizedApproaches)
                .when()
                    .put(aspspProfileServerUri + "/api/v1/aspsp-profile/for-debug/sca-approaches")
                .then()
                    .statusCode(HttpStatus.OK.value());

        this.memoizedApproaches = null;
    }
}
