package de.adorsys.opba.adminapi.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This class validates that admin-api is protected using BasicAuth.
 */
@Slf4j
public class AdminApiSecurityFilter implements Filter {

    private static final int BASIC_KEYWORD_LEN = 6;
    private final String expectedAuth;

    public AdminApiSecurityFilter(String login, String password) {
        this.expectedAuth = Base64.getEncoder().encodeToString((login + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        String authHeader = request.getHeader(AUTHORIZATION);
        if (Strings.isEmpty(authHeader) || !authHeader.startsWith("Basic ")) {
            log.warn("Missing or wrong Authorization header for {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization");
            return;
        }

        String authData = authHeader.substring(BASIC_KEYWORD_LEN);
        if (!expectedAuth.equals(authData)) {
            log.warn("Bad credentials for {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong credentials");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
