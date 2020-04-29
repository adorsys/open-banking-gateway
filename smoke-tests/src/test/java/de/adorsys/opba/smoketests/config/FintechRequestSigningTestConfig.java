package de.adorsys.opba.smoketests.config;

import de.adorsys.opba.api.security.external.EnableOpenBankingRequestSigning;
import de.adorsys.opba.api.security.external.config.RequestSigningConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;


@EnableOpenBankingRequestSigning
@ConfigurationProperties("security")
public class FintechRequestSigningTestConfig extends RequestSigningConfig {
}
