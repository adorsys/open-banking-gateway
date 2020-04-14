package de.adorsys.fintech.tests.e2e.config;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmokeConfig {

    @Getter
    @Value("${test.fintech.uri}")
    private String fintechServerUri;

    @Getter
    @Value("${test.fintech.search.uri}")
    private String fintechSearchUri;

    @Getter
    @ProvidedScenarioState
    private String actualUri;
}
