package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.api.security.EnableSignRequestBasedApiSecurity;
import de.adorsys.opba.api.security.config.RequestSigningConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@EnableSignRequestBasedApiSecurity
@ConfigurationProperties("security")
@Validated
public class FintechRequestSigningTestConfig extends RequestSigningConfig {
}
