package de.adorsys.opba.api.security.internal.filter;


import de.adorsys.opba.api.security.external.domain.FilterValidationHeaderValues;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.mapper.HttpRequestToDataToSignMapper;
import de.adorsys.opba.api.security.internal.config.OperationTypeProperties;
import de.adorsys.opba.api.security.internal.service.RequestVerifyingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class validates 'X-Request-Signature' header of the incoming request from fintech.
 * The signature verification happens: all required fields(headers/query params) must be present
 * and request timestamp should be within the range of security.verification.request-validity-window property.
 * The filter can be disabled by using 'no-signature-filter' spring active profile.
 */
@Slf4j
@RequiredArgsConstructor
public class RequestSignatureValidationFilter extends OncePerRequestFilter {
    public static final String OPBA_BANKING_PATH = "/v1/banking/**";

    private final RequestVerifyingService requestVerifyingService;
    private final Duration requestTimeLimit;
    private final ConcurrentHashMap<String, String> consumerKeysMap;
    private final OperationTypeProperties properties;

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final UrlPathHelper pathHelper = new UrlPathHelper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        FilterValidationHeaderValues headerValues = buildRequestValidationData(request);
        String expectedPath = properties.getAllowedPath().get(headerValues.getOperationType());

        if (isNotAllowedOperation(headerValues.getRequestPath(), expectedPath)) {
            log.error("Request operation type is not allowed");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong Operation Type");
            return;
        }

        Instant instant = Instant.parse(headerValues.getRequestTimeStamp());
        String fintechApiKey = consumerKeysMap.get(headerValues.getFintechId());

        if (fintechApiKey == null) {
            log.error("Api key for fintech ID {} has not find ", headerValues.getFintechId());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong Fintech ID");
            return;
        }

        boolean verificationResult = verifyRequestSignature(request, headerValues, instant, fintechApiKey);

        if (!verificationResult) {
            log.error("Signature verification error ");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Signature verification error");
            return;
        }

        if (isRequestExpired(instant)) {
            log.error("Timestamp validation failed");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Timestamp validation failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !matcher.match(OPBA_BANKING_PATH, pathHelper.getPathWithinApplication(request));
    }

    private boolean verifyRequestSignature(HttpServletRequest request, FilterValidationHeaderValues headerValues, Instant instant, String fintechApiKey) {
        HttpRequestToDataToSignMapper mapper = new HttpRequestToDataToSignMapper();
        OperationType operationType = OperationType.valueOf(headerValues.getOperationType());
        boolean verificationResult;

        switch (operationType) {
            case AIS:
                if (OperationType.isTransactionsPath(request.getRequestURI())) {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToListTransactions(request, instant));
                } else {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToListAccounts(request, instant));
                }
                break;
            case BANK_SEARCH:
                if (OperationType.isBankSearchPath(request.getRequestURI())) {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToBankSearch(request, instant));
                } else {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToBankProfile(request, instant));
                }
                break;
            case CONFIRM_CONSENT:
                verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToConfirmConsent(request, instant));
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operation type %s", operationType));
        }

        return verificationResult;
    }

    private boolean isRequestExpired(Instant operationTime) {
        Instant now = Instant.now();
        return now.plus(requestTimeLimit).isBefore(operationTime)
                       || now.minus(requestTimeLimit).isAfter(operationTime);
    }

    private boolean isNotAllowedOperation(String requestURI, String expectedPath) {
        return expectedPath == null || !requestURI.startsWith(expectedPath);
    }

    private FilterValidationHeaderValues buildRequestValidationData(HttpServletRequest request) {
        return FilterValidationHeaderValues.builder()
                       .fintechId(request.getHeader(HttpHeaders.FINTECH_ID))
                       .xRequestId(request.getHeader(HttpHeaders.X_REQUEST_ID))
                       .requestTimeStamp(request.getHeader(HttpHeaders.X_TIMESTAMP_UTC))
                       .operationType(request.getHeader(HttpHeaders.X_OPERATION_TYPE))
                       .xRequestSignature(request.getHeader(HttpHeaders.X_REQUEST_SIGNATURE))
                       .requestPath(request.getRequestURI())
                       .build();
    }
}
