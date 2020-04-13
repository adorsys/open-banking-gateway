package de.adorsys.opba.fintech.impl.web.filter;

import de.adorsys.opba.fintech.impl.service.RequestSigningService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class XRequestIdSigningFilter extends OncePerRequestFilter {
    private final RequestSigningService requestSigningService;

    public XRequestIdSigningFilter(RequestSigningService requestSigningService) {
        this.requestSigningService = requestSigningService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xRequestIdHeader = request.getHeader("X-Request-ID");
        HeaderModifyingRequestWrapper servletRequestWrapper = new HeaderModifyingRequestWrapper(request);

        if (xRequestIdHeader != null) {
            String encryptedHeader = requestSigningService.sign(xRequestIdHeader);
            servletRequestWrapper.addHeader("X-Request-ID", encryptedHeader);
        }

        filterChain.doFilter(servletRequestWrapper, response);
    }
}
