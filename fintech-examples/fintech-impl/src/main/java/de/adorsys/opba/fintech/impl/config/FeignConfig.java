package de.adorsys.opba.fintech.impl.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class enhances requests, so that the include PSU IP address headers. This header should either become
 * mandatory or we need to expose header-based configuration in API.
 * FIXME: https://github.com/adorsys/open-banking-gateway/issues/474
 * After aforementioned issue is fixed, this class should not exist.
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        // This allows OPBA Consent API to compute PSU IP address itself.
        return requestTemplate -> requestTemplate.header("Compute-PSU-IP-Address", "true");
    }
}
