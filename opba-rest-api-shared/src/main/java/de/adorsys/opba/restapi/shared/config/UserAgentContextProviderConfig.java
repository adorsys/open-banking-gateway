package de.adorsys.opba.restapi.shared.config;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.HttpServletRequest;

import static de.adorsys.opba.restapi.shared.HttpHeaders.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_ACCEPT;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_ACCEPT_CHARSET;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_ACCEPT_ENCODING;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_ACCEPT_LANGUAGE;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_DEVICE_ID;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_GEO_LOCATION;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_HTTP_METHOD;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_IP_PORT;
import static de.adorsys.opba.restapi.shared.HttpHeaders.UserAgentContext.PSU_USER_AGENT;

@Configuration
public class UserAgentContextProviderConfig {

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserAgentContext provideCurrentUserAgentContext(HttpServletRequest httpServletRequest) {
        return UserAgentContext.builder()
                .psuIpAddress("true".equals(httpServletRequest.getHeader(COMPUTE_PSU_IP_ADDRESS))
                        ? httpServletRequest.getRemoteAddr()
                        : httpServletRequest.getHeader(PSU_IP_ADDRESS))
                .psuIpPort(httpServletRequest.getHeader(PSU_IP_PORT))
                .psuAccept(httpServletRequest.getHeader(PSU_ACCEPT))
                .psuAcceptCharset(httpServletRequest.getHeader(PSU_ACCEPT_CHARSET))
                .psuAcceptEncoding(httpServletRequest.getHeader(PSU_ACCEPT_ENCODING))
                .psuAcceptLanguage(httpServletRequest.getHeader(PSU_ACCEPT_LANGUAGE))
                .psuDeviceId(httpServletRequest.getHeader(PSU_DEVICE_ID))
                .psuUserAgent(httpServletRequest.getHeader(PSU_USER_AGENT))
                .psuGeoLocation(httpServletRequest.getHeader(PSU_GEO_LOCATION))
                .psuHttpMethod(httpServletRequest.getHeader(PSU_HTTP_METHOD))
                .build();
    }
}
