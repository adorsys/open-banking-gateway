package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.EnableVerifySignatureBasedApiSecurity;
import de.adorsys.opba.api.security.filter.RequestSignatureValidationFilter;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
import de.adorsys.opba.api.security.service.impl.RsaJwtsVerifyingServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.util.Map;


@Data
@EnableVerifySignatureBasedApiSecurity
@ConfigurationProperties(prefix = "fintech.verification")
public class RequestVerifyingConfig {
    private Duration requestTimeLimit;
    private Map<String, String> fintechKeys;

    @Bean
    public RequestSignatureValidationFilter requestSignatureValidationFilter(Environment environment) {
        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl();
        return new RequestSignatureValidationFilter(requestVerifyingService, requestTimeLimit, fintechKeys);
    }
}
