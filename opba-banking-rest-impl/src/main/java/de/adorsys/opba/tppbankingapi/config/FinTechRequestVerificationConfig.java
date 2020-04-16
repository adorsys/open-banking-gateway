package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.filter.RequestSignatureValidationFilter;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
import de.adorsys.opba.api.security.service.impl.RsaJwtsVerifyingServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.time.Duration;

@Data
@Configuration
//@EnableVerifySignatureBasedApiSecurity
@PropertySource("classpath:fintech-db.yml")
@ConfigurationProperties(prefix = "fintech.verification")
public class FinTechRequestVerificationConfig {
    private Duration requestTimeLimit;

    @Bean
    public RequestSignatureValidationFilter requestSignatureValidationFilter(Environment environment) {
        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl();
        return new RequestSignatureValidationFilter(requestVerifyingService, requestTimeLimit, environment);
    }
}
