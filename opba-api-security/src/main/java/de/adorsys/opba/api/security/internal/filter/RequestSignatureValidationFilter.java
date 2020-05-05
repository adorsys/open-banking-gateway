package de.adorsys.opba.api.security.internal.filter;


import de.adorsys.opba.api.security.external.domain.DataToSign;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.internal.service.RequestVerifyingService;
import de.adorsys.opba.api.security.external.domain.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Order(1)
@RequiredArgsConstructor
public class RequestSignatureValidationFilter extends AbstractSecurityFilter {

    private final RequestVerifyingService requestVerifyingService;
    private final Duration requestTimeLimit;
    private final ConcurrentHashMap<String, String> consumerKeysMap;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String fintechId = request.getHeader(HttpHeaders.FINTECH_ID);
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String requestTimeStamp = request.getHeader(HttpHeaders.X_TIMESTAMP_UTC);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);
        String xRequestSignature = request.getHeader(HttpHeaders.X_REQUEST_SIGNATURE);

        Instant instant = Instant.parse(requestTimeStamp);

        String fintechApiKey = consumerKeysMap.get(fintechId);

        if (fintechApiKey == null) {
            log.error("Api key for fintech ID {} has not find ", fintechId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong Fintech ID");
            return;
        }

        DataToSign dataToSign = new DataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));

        boolean verificationResult = requestVerifyingService.verify(xRequestSignature, fintechApiKey, dataToSign);

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

    private boolean isRequestExpired(Instant operationTime) {
        Instant now = Instant.now();
        return now.plus(requestTimeLimit).isBefore(operationTime)
                       || now.minus(requestTimeLimit).isAfter(operationTime);
    }
}
