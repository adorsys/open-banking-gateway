package de.adorsys.opba.tppbankingapi.web.filter;

import de.adorsys.opba.protocol.facade.services.RequestVerifyingService;
import de.adorsys.opba.tppbankingapi.config.FinTechServicesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;

import static de.adorsys.opba.restapi.shared.HttpHeaders.FINTECH_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSignatureValidationFilter extends OncePerRequestFilter {
    private static final String X_REQUEST_SIGNATURE = "X-Request-Signature";

    private final RequestVerifyingService requestVerifyingService;
    private final FinTechServicesConfig finTechServicesConfig;

    @Value("${fintech.verification.request-time-limit}")
    private Duration timeLimit;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String fintechId = request.getHeader(FINTECH_ID);
        String xRequestSignature = request.getHeader(X_REQUEST_SIGNATURE);

        String fintechApiKey = finTechServicesConfig.getEnv().getProperty(fintechId);

        if (fintechApiKey == null) {
            log.warn("Api key for fintech ID {} has not find ", fintechId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Wrong Fintech ID");
            return;
        }

        String signData = requestVerifyingService.verify(xRequestSignature, fintechApiKey);

        if (signData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Signature verification error");
            return;
        }

        String requestTimeStamp = signData.substring(36);
        OffsetDateTime dateTime = OffsetDateTime.parse(requestTimeStamp);

        if (operationDateTimeNowWithinLimit(dateTime)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Timestamp validation failed");
            return;
        }


        filterChain.doFilter(request, response);
    }

    private boolean operationDateTimeNowWithinLimit(OffsetDateTime dateTime) {
        return OffsetDateTime.now().plus(timeLimit).isBefore(dateTime)
                       || OffsetDateTime.now().minus(timeLimit).isAfter(dateTime);
    }

}
