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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Data
@Validated
@EnableSignatureBasedApiSecurity
@ConfigurationProperties(prefix = BANKING_API_CONFIG_PREFIX + "security.verification")
public class RequestVerifyingConfig {

    @NotNull
    private Duration requestValidityWindow;

    @NotNull
    private ConcurrentHashMap<@NotBlank String, @NotBlank String> consumerPublicKeys;

    @NotBlank
    private String claimNameKey;

    @NotEmpty
    private Set<@NotBlank String> urlsToBeSkipped;

    @Bean
    @Profile("!no-signature-filter")
    public FilterRegistrationBean<RequestSignatureValidationFilter> requestSignatureValidationFilter(OperationTypeProperties properties) {

        RequestVerifyingService requestVerifyingService = new RsaJwtsVerifyingServiceImpl(claimNameKey);
        FilterRegistrationBean<RequestSignatureValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestSignatureValidationFilter(urlsToBeSkipped, requestVerifyingService, requestValidityWindow, consumerPublicKeys, properties));

        return registrationBean;
    }
}
