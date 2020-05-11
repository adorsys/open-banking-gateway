package de.adorsys.opba.tppauthapi.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CookieRenewalFilterConfig {
    @Bean
    public FilterRegistrationBean<CookieRenewalFilter> cookieRenewalFilterFilter() {
        log.debug("INIT COOKIE RENEWAL FILTER3");
        FilterRegistrationBean<CookieRenewalFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new CookieRenewalFilter());
        // registrationBean.addUrlPatterns("/v1/psu/ais/*/renewal-authorization-session-key");
        // pattern does not work, for that pattern is checked in filter again
        registrationBean.addUrlPatterns("/v1/psu/ais/*");

        return registrationBean;
    }
}
