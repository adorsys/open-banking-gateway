package de.adorsys.opba.consentapi.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.config.AuthorizationSessionKeyConfig;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableTokenBasedApiSecurity
@RequiredArgsConstructor
public class ConsentAuthConfig {

    private final AuthorizationSessionKeyConfig.AuthorizationSessionKeyFromHttpRequest authorizationSessionKey;

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FacadeServiceableRequest provideCurrentFacadeServiceable(HttpServletRequest httpServletRequest) {
        return FacadeServiceableRequest.builder()
                .authorizationKey(authorizationSessionKey.getKey())
                .build();
    }
}
