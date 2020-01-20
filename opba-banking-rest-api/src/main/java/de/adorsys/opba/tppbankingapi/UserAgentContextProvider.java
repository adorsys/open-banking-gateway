package de.adorsys.opba.tppbankingapi;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class UserAgentContextProvider {

    public UserAgentContext provideCurrentUserAgentContext(HttpServletRequest httpServletRequest) {
        String psuIpAddress = httpServletRequest.getHeader("PSU-IP-Address");

        return new UserAgentContext(psuIpAddress != null ? psuIpAddress : "");
    }
}
