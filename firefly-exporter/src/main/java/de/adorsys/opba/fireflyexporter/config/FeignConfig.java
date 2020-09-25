package de.adorsys.opba.fireflyexporter.config;

import com.google.common.collect.Iterables;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.RequestDataToSignNormalizer;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import de.adorsys.opba.api.security.requestsigner.OpenBankingDataToSignProvider;
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

import static de.adorsys.opba.fireflyexporter.config.HeaderFields.COMPUTE_PSU_IP_ADDRESS;
import static de.adorsys.opba.fireflyexporter.config.HeaderFields.FINTECH_ID;
import static de.adorsys.opba.fireflyexporter.config.HeaderFields.X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fireflyexporter.config.HeaderFields.X_TIMESTAMP_UTC;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final RequestSigningService requestSigningService;
    private final OpenBankingConfig opbaConfig;

    @Bean
    public RequestInterceptor requestInterceptorWithSigning() {
        return requestTemplate -> {
            requestTemplate.header(COMPUTE_PSU_IP_ADDRESS, Boolean.TRUE.toString());
            fillSecurityHeadersWithSigning(requestTemplate);
        };
    }

    private void fillSecurityHeadersWithSigning(RequestTemplate requestTemplate) {
        Instant instant = Instant.now();

        requestTemplate.header(FINTECH_ID, opbaConfig.getClientId());
        requestTemplate.header(X_TIMESTAMP_UTC, instant.toString());
        requestTemplate.header(X_REQUEST_SIGNATURE, calculateSignature(requestTemplate));
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
                .body(new String(requestTemplate.body(), requestTemplate.requestCharset()))
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
