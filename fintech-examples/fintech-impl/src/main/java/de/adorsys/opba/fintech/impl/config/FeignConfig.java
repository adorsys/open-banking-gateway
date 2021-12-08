package de.adorsys.opba.fintech.impl.config;

import com.google.common.collect.Iterables;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.AisErrorDecoder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static de.adorsys.opba.api.security.SecurityGlobalConst.DISABLED_SECURITY_OR_ENABLED_NO_SIGNATURE_FILTER_PROFILE;
import static de.adorsys.opba.api.security.SecurityGlobalConst.ENABLED_SECURITY_AND_DISABLED_NO_SIGNATURE_FILTER_PROFILE;
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

    private final RequestSigningService requestSigningService;
    private final TppProperties tppProperties;

    @Bean
    public AisErrorDecoder aisErrorDecoder() {
        return new AisErrorDecoder();
    }

    @Bean
    @Profile(ENABLED_SECURITY_AND_DISABLED_NO_SIGNATURE_FILTER_PROFILE)
    public RequestInterceptor requestInterceptorWithSigning() {
        // This allows OPBA Consent API to compute PSU IP address itself.
        return requestTemplate -> {
            requestTemplate.header(COMPUTE_PSU_IP_ADDRESS, "true");
            fillSecurityHeadersWithSigning(requestTemplate);
        };
    }

    @Bean
    @Profile(DISABLED_SECURITY_OR_ENABLED_NO_SIGNATURE_FILTER_PROFILE)
    public RequestInterceptor requestInterceptorWithoutSigning() {
        // This allows OPBA Consent API to compute PSU IP address itself.
        return requestTemplate -> {
            requestTemplate.header(COMPUTE_PSU_IP_ADDRESS, "true");
            fillSecurityHeadersWithoutSigning(requestTemplate);
        };
    }

    private void fillSecurityHeadersWithSigning(RequestTemplate requestTemplate) {
        Instant instant = Instant.now();

        requestTemplate.header(FINTECH_ID, tppProperties.getFintechID());
        requestTemplate.header(X_TIMESTAMP_UTC, instant.toString());
        requestTemplate.header(X_REQUEST_SIGNATURE, calculateSignature(requestTemplate));
    }

    private void fillSecurityHeadersWithoutSigning(RequestTemplate requestTemplate) {
        Instant instant = Instant.now();

        requestTemplate.header(FINTECH_ID, tppProperties.getFintechID());
        requestTemplate.header(X_TIMESTAMP_UTC, instant.toString());
    }

    private String calculateSignature(RequestTemplate requestTemplate) {
        Map<String, String> headers = requestTemplate.headers().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, it -> Iterables.getFirst(it.getValue(), ""))
        );
        Map<String, String> queries = requestTemplate.queries().entrySet().stream()
                                              .collect(Collectors.toMap(Map.Entry::getKey, e -> decodeQueryValue(e.getValue())));

        // OpenBankingSigner - This is generated class by opba-api-security-signer-generator-impl annotation processor
        DataToSignProvider dataToSignProvider = new OpenBankingDataToSignProvider();
        RequestToSign toSign = RequestToSign.builder()
                .method(DataToSignProvider.HttpMethod.valueOf(requestTemplate.method()))
                .path(requestTemplate.path())
                .headers(headers)
                .queryParams(queries)
                .body(requestTemplate.requestBody().asString())
                .build();
        RequestDataToSignNormalizer signatureGen = dataToSignProvider.normalizerFor(toSign);
        return requestSigningService.signature(signatureGen.canonicalStringToSign(toSign));
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
