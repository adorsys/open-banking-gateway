package de.adorsys.opba.api.security.internal.filter;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
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
/**
 * This Filter removes all requests that dont have security cookie key
 * But security check itself is not done. This is done in
 * AuthorizationSessionKeyConfig
 */
@Slf4j
@Order(1)
public class SecurityCookieFilter implements Filter {
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = httpServletRequest.getRequestURI();
        if (!isForRenewal(uri) && !isForConsent(uri)) {
            chain.doFilter(request, response);
            return;
        }
        if (!isCookieAvailable(httpServletRequest)) {
            if (isForRenewal(uri)) {
                log.debug("no cookie available for {}, {}. call is denied", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
            } else {
                log.warn("no cookie available for {}, {}. call is denied", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
            }
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        log.debug("cookie available for {}{}.", httpServletRequest.getMethod(), uri);
        chain.doFilter(request, response);
    }

    private boolean isForRenewal(String path) {
        return path.matches(".*/v1/psu/ais/.+/renewal-authorization-session-key$");
    }

    private boolean isForConsent(String path) {
        return path.matches(".*/v1/consent/.+$");
    }
    private boolean isCookieAvailable(HttpServletRequest httpServletRequest) {
        return getCookieValue(httpServletRequest) != null;
    }
    private String getCookieValue(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() == null) {
            return null;
        }
        return Arrays.stream(httpServletRequest.getCookies())
                    .filter(it -> HttpHeaders.AUTHORIZATION_SESSION_KEY.equals(it.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
    }
}
