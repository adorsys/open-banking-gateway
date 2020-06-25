package de.adorsys.opba.api.security.internal.config;

import de.adorsys.opba.api.security.internal.filter.HttpBodyCachingFilter;
import de.adorsys.opba.api.security.internal.filter.RequestCookieFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RequestFilterConfig {

    @Bean
    public FilterRegistrationBean<HttpBodyCachingFilter> httpBodyCachingFilter() {
        FilterRegistrationBean<HttpBodyCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpBodyCachingFilter());
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestCookieFilter> cookieValidatorFilter(CookieProperties cookieProperties) {
        Set<String> urlsToBeValidated = cookieProperties.getUrlsToBeValidated();

        FilterRegistrationBean<RequestCookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestCookieFilter(urlsToBeValidated));
        registrationBean.setUrlPatterns(urlsToBeValidated);
        registrationBean.setOrder(2);

        return registrationBean;
    }
}
