package de.adorsys.opba.tppauthapi.config;

import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.API_CONFIG_PREFIX;

@SpringBootApplication
@EnableConfigurationProperties
@EnableBankingPersistence
@Configuration
public class ApplicationTest {

    @Validated
    @Configuration
    @ConfigurationProperties(API_CONFIG_PREFIX + "security")
    public class TppTokenPropertiesConfig extends TppTokenProperties {
    }
}
