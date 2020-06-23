package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.external.EnableOpenBankingRequestSigning;
import de.adorsys.opba.api.security.external.config.RequestSigningConfig;
import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.api.security.internal.service.CookieBuilderTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;

@EnableOpenBankingRequestSigning
@ConfigurationProperties("security")
@Validated
public class FintechRequestSigningTestConfig extends RequestSigningConfig {
    @Configuration
    @EnableTokenBasedApiSecurity
    public class TppPsuAuthConfig {
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        CookieBuilderTemplate tppAuthResponseCookieBuilder(CookieProperties cookieProperties) {
            return new CookieBuilderTemplate(cookieProperties);
        }
    }
}
