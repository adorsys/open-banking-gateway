package de.adorsys.opba.fireflyexporter.config;

import de.adorsys.opba.api.security.external.EnableOpenBankingRequestSigning;
import de.adorsys.opba.api.security.external.config.RequestSigningConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@EnableOpenBankingRequestSigning
@ConfigurationProperties("security")
@Validated
public class OpenBankingRequestSigningConfig extends RequestSigningConfig {
}
