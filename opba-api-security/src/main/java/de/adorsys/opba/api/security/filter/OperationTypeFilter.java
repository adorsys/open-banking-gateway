package de.adorsys.opba.api.security.filter;

import de.adorsys.opba.api.security.config.OperationTypeProperties;
import de.adorsys.opba.api.security.domain.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(0)
@Component
@RequiredArgsConstructor
public class OperationTypeFilter extends AbstractSecurityFilter {

    private final OperationTypeProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);
        String requestPath = request.getRequestURI();
        String expectedPath = properties.getAllowedPath().get(operationType);

        if (isNotAllowedOperation(requestPath, expectedPath)) {
            log.error("Request operation type is not allowed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isNotAllowedOperation(String requestURI, String expectedPath) {
        return expectedPath == null || !requestURI.startsWith(expectedPath);
    }
}
