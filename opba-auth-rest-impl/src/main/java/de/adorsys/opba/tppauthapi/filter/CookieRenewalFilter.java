package de.adorsys.opba.tppauthapi.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static de.adorsys.opba.restapi.shared.HttpHeaders.AUTHORIZATION_SESSION_KEY;

@Slf4j
@Order(1)
public class CookieRenewalFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        log.debug("Request {} : {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());

        if (httpServletRequest.getRequestURI().endsWith("renewal-authorization-session-key")) {
            String authCookieValue = null;
            if (httpServletRequest.getCookies() != null) {
                authCookieValue = Arrays.stream(httpServletRequest.getCookies())
                        .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
            }
            if (authCookieValue == null) {
                log.debug("no cookie available");
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            log.debug("cookie available");
        }
        chain.doFilter(request, response);
    }
}
