package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.internal.EnableSignatureBasedApiSecurity;
import de.adorsys.opba.api.security.internal.config.OperationTypeProperties;
import de.adorsys.opba.api.security.internal.filter.RequestSignatureValidationFilter;
import de.adorsys.opba.api.security.internal.service.RequestVerifyingService;
import de.adorsys.opba.api.security.internal.service.RsaJwtsVerifyingServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Data
@Validated
@EnableSignatureBasedApiSecurity
@ConfigurationProperties(prefix = BANKING_API_CONFIG_PREFIX + "security.verification")
public class RequestVerifyingConfig {

    @NotNull
    private Duration requestValidityWindow;

    @NotNull
    private ConcurrentMap<@NotBlank String, @NotNull ApiConsumer> consumers;

    @NotBlank
    private String claimNameKey;

    @NotEmpty
    private Set<@NotBlank String> urlsToBeValidated;

    @Bean
    @Profile("!no-signature-filter")
    public FilterRegistrationBean<RequestSignatureValidationFilter> requestSignatureValidationFilter(OperationTypeProperties properties) {

        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl(claimNameKey);
        FilterRegistrationBean<RequestSignatureValidationFilter> registrationBean = new FilterRegistrationBean<>();

        ConcurrentMap<String, String> consumerKeysMap = consumers.entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> e.getValue().getPublicKey()));

        registrationBean.setFilter(new RequestSignatureValidationFilter(
                requestVerifyingService, requestValidityWindow, consumerKeysMap, properties));
        registrationBean.setUrlPatterns(urlsToBeValidated);

        return registrationBean;
    }

    @Data
    public static class ApiConsumer {
        @NotBlank
        private String name;

        @NotBlank
        private String publicKey;
    }
}
