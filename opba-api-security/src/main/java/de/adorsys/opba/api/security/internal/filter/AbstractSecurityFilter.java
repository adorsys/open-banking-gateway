package de.adorsys.opba.api.security.internal.filter;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractSecurityFilter extends OncePerRequestFilter {
    public static final String OPBA_BANKING_PATH = "/v1/banking/**";

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final UrlPathHelper pathHelper = new UrlPathHelper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !matcher.match(OPBA_BANKING_PATH, pathHelper.getPathWithinApplication(request));
    }
}
