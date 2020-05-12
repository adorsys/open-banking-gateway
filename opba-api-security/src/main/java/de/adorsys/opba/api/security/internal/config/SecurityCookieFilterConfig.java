package de.adorsys.opba.api.security.internal.config;

import de.adorsys.opba.api.security.internal.filter.SecurityCookieFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecurityCookieFilterConfig {
    @Bean
    public FilterRegistrationBean<SecurityCookieFilter> cookieRenewalFilterFilter() {
        log.debug("INIT AUTH FILTER");
        FilterRegistrationBean<SecurityCookieFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SecurityCookieFilter());
        registrationBean.addUrlPatterns("/v1/psu/ais/*", "/v1/consent/*");

        return registrationBean;
    }
}
