package de.adorsys.fintech.tests.e2e.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SmokeConfig {

    @Getter
    @Value("${test.smoke.fintech.server-uri}")
    private String fintechServerUri;
}
