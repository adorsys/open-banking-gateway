package de.adorsys.opba.fintech.impl.web.filter;

import de.adorsys.opba.fintech.impl.service.DataEncryptionService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class XRequestIdSigningFilter extends OncePerRequestFilter {
    private final DataEncryptionService dataEncryptionService;

    public XRequestIdSigningFilter(DataEncryptionService dataEncryptionService) {
        this.dataEncryptionService = dataEncryptionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xRequestIdHeader = request.getHeader("X-Request-ID");
        HeaderModifyingRequestWrapper servletRequestWrapper = new HeaderModifyingRequestWrapper(request);

        if (xRequestIdHeader != null) {
            String encryptedHeader = dataEncryptionService.encrypt(xRequestIdHeader);
            servletRequestWrapper.addHeader("X-Request-ID", encryptedHeader);
        }

        filterChain.doFilter(servletRequestWrapper, response);
    }
}
