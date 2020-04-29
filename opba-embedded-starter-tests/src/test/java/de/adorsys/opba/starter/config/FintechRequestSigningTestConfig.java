package de.adorsys.opba.starter.config;

import de.adorsys.opba.api.security.external.EnableOpenBankingRequestSigning;
import de.adorsys.opba.api.security.external.config.RequestSigningConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@EnableOpenBankingRequestSigning
@ConfigurationProperties("security")
@Validated
public class FintechRequestSigningTestConfig extends RequestSigningConfig {
}
