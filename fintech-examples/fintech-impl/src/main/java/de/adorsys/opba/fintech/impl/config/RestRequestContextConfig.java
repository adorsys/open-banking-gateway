package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Configuration
public class RestRequestContextConfig {
    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RestRequestContext provideCurrentRestRequest(HttpServletRequest httpServletRequest) {
        String sessionCookieValue = null;
        if (httpServletRequest.getCookies() != null) {
            Optional<Cookie> sessionCookie = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> Consts.COOKIE_SESSION_COOKIE_NAME.equalsIgnoreCase(cookie.getName())).findFirst();
            if (sessionCookie.isPresent()) {
                sessionCookieValue = sessionCookie.get().getValue();
            }
        }
        return RestRequestContext.builder()
                .sessionCookieValue(sessionCookieValue)
                .xsrfTokenHeaderField(httpServletRequest.getHeader(Consts.HEADER_XSRF_TOKEN))
                .requestId(httpServletRequest.getHeader(Consts.HEADER_X_REQUEST_ID))
                .build();
    }

}
