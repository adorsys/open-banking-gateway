package de.adorsys.opba.tppauthapi.config;

import de.adorsys.opba.tppauthapi.controller.TppAuthResponseCookieTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AuthConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    TppAuthResponseCookieTemplate tppAuthResponseCookieBuilder(CookieProperties cookieProperties) {
        return new TppAuthResponseCookieTemplate(cookieProperties);
    }
}
