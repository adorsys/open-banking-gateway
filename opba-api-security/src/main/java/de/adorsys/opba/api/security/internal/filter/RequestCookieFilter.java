package de.adorsys.opba.api.security.internal.filter;

import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_ID;

/**
 * This Filter removes all requests that dont have security cookie key
 * But security check itself is not done. This is done in
 * AuthorizationSessionKeyConfig
 */
@Slf4j
public class RequestCookieFilter implements Filter {
    private final Set<String> urlsToBeValidated;

    public RequestCookieFilter(Set<String> urlsToBeValidated) {
        this.urlsToBeValidated = urlsToBeValidated;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (shouldNotFilter(uri)
                    || isCookieAvailable(request.getCookies())) {

            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Cookie is required for the request [{}] {} - {} but it was not provided!", request.getHeader(X_REQUEST_ID), request.getMethod(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean shouldNotFilter(String uri) {
        return urlsToBeValidated.stream()
                       .noneMatch(uri::matches);
    }

    private boolean isCookieAvailable(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                       .filter(it -> AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                       .findFirst()
                       .map(Cookie::getValue)
                       .isPresent();
    }
}
