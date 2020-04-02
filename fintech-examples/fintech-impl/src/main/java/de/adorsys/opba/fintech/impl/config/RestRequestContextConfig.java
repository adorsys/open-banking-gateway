package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
public class RestRequestContextConfig {
    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RestRequestContext provideCurrentRestRequest(HttpServletRequest httpServletRequest) {
        return RestRequestContext.builder()
                .sessionCookieValue(Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> cookie.getName().equalsIgnoreCase(Consts.COOKIE_SESSION_COOKIE_NAME)).findFirst().get().getValue())
                .xsrfTokenHeaderField(httpServletRequest.getHeader(Consts.HEADER_XSRF_TOKEN))
                .requestId(httpServletRequest.getHeader(Consts.HEADER_X_REQUEST_ID))
                .build();
    }

}
