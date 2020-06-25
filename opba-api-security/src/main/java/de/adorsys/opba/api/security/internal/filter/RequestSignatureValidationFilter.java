package de.adorsys.opba.api.security.internal.filter;


import de.adorsys.opba.api.security.external.domain.FilterValidationHeaderValues;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.mapper.HttpRequestToDataToSignMapper;
import de.adorsys.opba.api.security.internal.config.OperationTypeProperties;
import de.adorsys.opba.api.security.internal.service.RequestVerifyingService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class validates 'X-Request-Signature' header of the incoming request from fintech.
 * The signature verification happens: all required fields(headers/query params) must be present
 * and request timestamp should be within the range of security.verification.request-validity-window property.
 * The filter can be disabled by using 'no-signature-filter' spring active profile.
 */
@Slf4j
public class RequestSignatureValidationFilter implements Filter {
    private final Set<String> urlsToBeSkipped;
    private final RequestVerifyingService requestVerifyingService;
    private final Duration requestTimeLimit;
    private final ConcurrentHashMap<String, String> consumerKeysMap;
    private final OperationTypeProperties properties;

    public RequestSignatureValidationFilter(Set<String> urlsToBeSkipped, RequestVerifyingService requestVerifyingService, Duration requestTimeLimit,
                                            ConcurrentHashMap<String, String> consumerKeysMap, OperationTypeProperties properties) {
        this.urlsToBeSkipped = urlsToBeSkipped;
        this.requestVerifyingService = requestVerifyingService;
        this.requestTimeLimit = requestTimeLimit;
        this.consumerKeysMap = consumerKeysMap;
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isAvoidFilterCheck(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        FilterValidationHeaderValues headerValues = buildRequestValidationData(request);
        String expectedPath = properties.getAllowedPath().get(headerValues.getOperationType());

        if (isInvalidOperationType(headerValues, expectedPath, response)) {
            return;
        }

        if (isUnparsableRequestTimeStamp(headerValues.getRequestTimeStamp(), response)
                    || isMissingFintechId(headerValues.getFintechId(), response)
                    || isUnparsableXRequestId(headerValues.getXRequestId(), response)) {
            return;
        }

        Instant instant = Instant.parse(headerValues.getRequestTimeStamp());
        String fintechApiKey = consumerKeysMap.get(headerValues.getFintechId());

        if (isMissingFintechApiKey(fintechApiKey, headerValues.getFintechId(), response)) {
            return;
        }

        boolean verificationResult = verifyRequestSignature(request, headerValues, instant, fintechApiKey);

        if (validateVerificationResult(verificationResult, response) && validateExpirationDate(instant, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isAvoidFilterCheck(String uri) {
        return urlsToBeSkipped.stream()
                       .anyMatch(uri::matches);
    }

    private boolean isInvalidOperationType(FilterValidationHeaderValues headerValues, String expectedPath, HttpServletResponse response) throws IOException {
        if (isNotAllowedOperation(headerValues.getRequestPath(), expectedPath)) {
            log.error("Request operation type is not allowed");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong Operation Type");
            return true;
        }

        return false;
    }

    private boolean isMissingFintechApiKey(String fintechApiKey, String fintechId, HttpServletResponse response) throws IOException {
        if (fintechApiKey == null) {
            log.error("Api key for fintech ID {} has not found ", fintechId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong Fintech ID");
            return true;
        }

        return false;
    }

    private boolean isUnparsableRequestTimeStamp(String requestTimeStamp, HttpServletResponse response) throws IOException {
        if (requestTimeStamp == null) {
            log.error("Required 'X-Timestamp-UTC' header is missing");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "X-Timestamp-UTC is missing");
            return true;
        }

        try {
            Instant.parse(requestTimeStamp);
        } catch (DateTimeParseException e) {
            log.error("'X-Timestamp-UTC' header is not a valid ISO8601 date");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong X-Timestamp-UTC");
            return true;
        }

        return false;
    }

    private boolean isUnparsableXRequestId(String xRequestId, HttpServletResponse response) throws IOException {
        if (xRequestId == null) {
            log.error("Required 'X-Request-ID' header is missing");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "X-Request-ID is missing");
            return true;
        }

        try {
            UUID.fromString(xRequestId);
        } catch (IllegalArgumentException e) {
            log.error("''X-Request-ID' header is not a valid UUID");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong X-Request-ID");
            return true;
        }

        return false;
    }

    private boolean isMissingFintechId(String fintechId, HttpServletResponse response) throws IOException {
        if (fintechId == null) {
            log.error("'Fintech-ID' header is missing");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fintech ID is missing");
            return true;
        }

        return false;
    }

    private boolean validateVerificationResult(boolean verificationResult, HttpServletResponse response) throws IOException {
        if (!verificationResult) {
            log.error("Signature verification error ");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Signature verification error");
            return false;
        }

        return true;
    }

    private boolean validateExpirationDate(Instant instant, HttpServletResponse response) throws IOException {
        if (isRequestExpired(instant)) {
            log.error("Timestamp validation failed");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Timestamp validation failed");
            return false;
        }

        return true;
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
            case PIS:
                if (OperationType.isGetPaymentStatus(request.getRequestURI())) {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToGetPaymentStatus(request, instant));
                } else if (OperationType.isGetPayment(request.getMethod())) {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToGetPayment(request, instant));
                } else {
                    verificationResult = requestVerifyingService.verify(headerValues.getXRequestSignature(), fintechApiKey, mapper.mapToPaymentInititation(request, instant));
                }
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
