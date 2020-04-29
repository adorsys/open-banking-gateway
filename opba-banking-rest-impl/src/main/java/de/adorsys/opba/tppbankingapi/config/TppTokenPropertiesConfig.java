package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.API_CONFIG_PREFIX;

@Validated
@Configuration
@ConfigurationProperties(API_CONFIG_PREFIX + "security")
public class TppTokenPropertiesConfig extends TppTokenProperties {
}
