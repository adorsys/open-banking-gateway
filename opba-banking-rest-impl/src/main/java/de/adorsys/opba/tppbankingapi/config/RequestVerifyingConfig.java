package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.EnableVerifySignatureBasedApiSecurity;
import de.adorsys.opba.api.security.filter.RequestSignatureValidationFilter;
import de.adorsys.opba.api.security.service.RequestVerifyingService;
import de.adorsys.opba.api.security.service.impl.RsaJwtsVerifyingServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;


@Data
@EnableVerifySignatureBasedApiSecurity
@ConfigurationProperties(prefix = "fintech.verification")
public class RequestVerifyingConfig {

    private Duration requestValidityWindow;
    private ConcurrentHashMap<String, String> fintechKeys;
    private String claimNameKey;

    @Bean
    public RequestSignatureValidationFilter requestSignatureValidationFilter() {
        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl(claimNameKey);
        return new RequestSignatureValidationFilter(requestVerifyingService, requestValidityWindow, fintechKeys);
    }
}
