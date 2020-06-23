package de.adorsys.opba.tppauthapi.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.config.CookieProperties;
import de.adorsys.opba.api.security.internal.service.CookieBuilderTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableTokenBasedApiSecurity
public class TppPsuAuthConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    CookieBuilderTemplate tppAuthResponseCookieBuilder(CookieProperties cookieProperties) {
        return new CookieBuilderTemplate(cookieProperties);
    }
}
