package de.adorsys.opba.tppbankingapi;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_ACCEPT;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_ACCEPT_CHARSET;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_ACCEPT_ENCODING;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_ACCEPT_LANGUAGE;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_DEVICE_ID;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_GEO_LOCATION;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_HTTP_METHOD;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_IP_ADDRESS;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_IP_PORT;
import static de.adorsys.opba.tppbankingapi.HttpHeaders.UserAgentContext.PSU_USER_AGENT;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class UserAgentContextProvider {

    public UserAgentContext provideCurrentUserAgentContext(HttpServletRequest httpServletRequest) {
        return UserAgentContext.builder()
                .psuIpAddress(httpServletRequest.getHeader(PSU_IP_ADDRESS))
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
