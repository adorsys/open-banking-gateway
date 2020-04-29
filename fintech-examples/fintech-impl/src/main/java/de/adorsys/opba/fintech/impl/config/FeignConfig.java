package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.api.security.external.domain.DataToSign;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.domain.OperationType;
import de.adorsys.opba.api.security.service.RequestSigningService;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.UUID;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FINTECH_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_OPERATION_TYPE;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.X_TIMESTAMP_UTC;

/**
 * This class enhances requests, so that the include PSU IP address headers. This header should either become
 * mandatory or we need to expose header-based configuration in API.
 * FIXME: https://github.com/adorsys/open-banking-gateway/issues/474
 * After aforementioned issue is fixed, this class should not exist.
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final RequestSigningService requestSigningService;
    private final TppProperties tppProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        // This allows OPBA Consent API to compute PSU IP address itself.
        return requestTemplate -> {
            requestTemplate.header(COMPUTE_PSU_IP_ADDRESS, "true");
            fillSecurityHeaders(requestTemplate);
        };
    }

    private void fillSecurityHeaders(RequestTemplate requestTemplate) {
        Instant instant = Instant.now();
        requestTemplate.header(X_REQUEST_SIGNATURE, calculateSignature(requestTemplate.request(), instant));
        requestTemplate.header(FINTECH_ID, tppProperties.getFintechID());
        requestTemplate.header(X_TIMESTAMP_UTC, instant.toString());
    }

    private String calculateSignature(Request request, Instant instant) {
        String xRequestId = request.headers().get(X_REQUEST_ID).stream().findFirst().orElse(null);
        String operationType = request.headers().get(X_OPERATION_TYPE).stream().findFirst().orElse(null);
        DataToSign dataToSign = new DataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
        return requestSigningService.signature(dataToSign);
    }
}
