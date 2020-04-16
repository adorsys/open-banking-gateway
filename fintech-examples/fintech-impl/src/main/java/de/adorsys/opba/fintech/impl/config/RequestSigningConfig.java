package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.api.security.service.RequestSigningService;
import de.adorsys.opba.api.security.service.impl.RsaJwtsSigningServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
//@EnableSignRequestBasedApiSecurity
@ComponentScan("de.adorsys.opba.api.security.service")
@ConfigurationProperties("security")
public class RequestSigningConfig {
    private String privateKey;
    private String signIssuer;
    private String signSubject;

    @Bean
    public RequestSigningService requestSigningService() {
        return new RsaJwtsSigningServiceImpl(privateKey, signIssuer, signSubject);

    }
}
