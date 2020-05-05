package de.adorsys.opba.consentapi.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.service.TokenBasedAuthService;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@EnableTokenBasedApiSecurity
@RequiredArgsConstructor
@Getter
public class ConsentAuthConfig {

    private final TokenBasedAuthService authService;
    private String authCookieValue;

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FacadeServiceableRequest provideCurrentFacadeServiceable(HttpServletRequest httpServletRequest) {
        if (null == httpServletRequest.getCookies()) {
            return FacadeServiceableRequest.builder().build();
        }

        authCookieValue = Arrays.stream(httpServletRequest.getCookies())
                .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalStateException("Authorization-Session-Key cookie is missing"));

        String subjectValue = authService.validateTokenAndGetSubject(authCookieValue);

        return FacadeServiceableRequest.builder()
                .authorizationKey(subjectValue)
                .build();
    }
}
