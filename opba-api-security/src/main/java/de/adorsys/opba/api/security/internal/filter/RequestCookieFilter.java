package de.adorsys.opba.api.security.internal.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;

/**
 * This Filter removes all requests that dont have security cookie key
 * But security check itself is not done. This is done in
 * AuthorizationSessionKeyConfig
 */
@Slf4j
@RequiredArgsConstructor
public class RequestCookieFilter extends OncePerRequestFilter {
    private final Set<String> urlsToBeValidated;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (skipCookieCheck(uri)
                    || isCookieAvailable(request.getCookies())) {

            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Cookie is required for the request {} - {} but it was not provided!", request.getMethod(), request.getRequestURI());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private boolean skipCookieCheck(String uri) {
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
