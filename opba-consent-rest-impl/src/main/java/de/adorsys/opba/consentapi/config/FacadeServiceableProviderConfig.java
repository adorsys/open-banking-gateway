package de.adorsys.opba.consentapi.config;

import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;

@Configuration
public class FacadeServiceableProviderConfig {

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FacadeServiceableRequest provideCurrentFacadeServiceable(HttpServletRequest httpServletRequest) {
        return FacadeServiceableRequest.builder()
                .authorizationKey(
                        Arrays.stream(httpServletRequest.getCookies())
                                .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                                .findFirst()
                                .map(Cookie::getValue)
                                .orElse(null)
                )
                .build();
    }
}
