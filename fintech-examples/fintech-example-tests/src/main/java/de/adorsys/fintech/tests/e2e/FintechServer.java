package de.adorsys.fintech.tests.e2e;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;

@JGivenStage
@Slf4j
public class FintechServer<SELF extends FintechServer<SELF>> extends  WebDriverBasedUserInfoFintech<SELF> {
    @BeforeStage
    void prepareRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        //    RestAssured.config = RestAssured.config().connectionConfig(ConnectionConfig.connectionConfig());
    }
}

