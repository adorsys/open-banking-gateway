package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.mapper.FeignTemplateToDataToSignMapper;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.FINTECH_ID;
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
    private static final String MISSING_HEADER_ERROR_MESSAGE = " header is missing";

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
        requestTemplate.header(X_REQUEST_SIGNATURE, calculateSignature(requestTemplate, instant));
        requestTemplate.header(FINTECH_ID, tppProperties.getFintechID());
        requestTemplate.header(X_TIMESTAMP_UTC, instant.toString());
    }

    private String calculateSignature(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();
        String requestOperationType = headers.get(HttpHeaders.X_OPERATION_TYPE).stream().findFirst()
                                              .orElseThrow(() -> new IllegalStateException(HttpHeaders.X_OPERATION_TYPE + MISSING_HEADER_ERROR_MESSAGE));
        OperationType operationType = OperationType.valueOf(requestOperationType);
        FeignTemplateToDataToSignMapper mapper = new FeignTemplateToDataToSignMapper();
        Map<String, String> queries = requestTemplate.queries().entrySet().stream()
                                              .collect(Collectors.toMap(Map.Entry::getKey, e -> decodeQueryValue(e.getValue())));

        switch (operationType) {
            case AIS:
                if (OperationType.isTransactionsPath(requestTemplate.path())) {
                    return requestSigningService.signature(mapper.mapToListTransactions(headers, queries, instant));
                }
                return requestSigningService.signature(mapper.mapToListAccounts(headers, instant));
            case BANK_SEARCH:
                if (OperationType.isBankSearchPath(requestTemplate.path())) {
                    return requestSigningService.signature(mapper.mapToBankSearch(headers, queries, instant));
                }
                return requestSigningService.signature(mapper.mapToBankProfile(headers, instant));
            case CONFIRM_CONSENT:
                return requestSigningService.signature(mapper.mapToConfirmConsent(headers, instant));
            case PIS:
                return requestSigningService.signature(mapper.mapToPaymentInitiation(headers, instant));
            default:
                throw new IllegalArgumentException(String.format("Unsupported operation type %s", operationType));
        }
    }

    private String decodeQueryValue(Collection<String> value) {
        if (value == null) {
            return null;
        }

        return value.stream()
                       .findFirst()
                       .map(this::decodeQueryParam)
                       .orElse(null);
    }


    private String decodeQueryParam(String param) {
        try {
            return URLDecoder.decode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
