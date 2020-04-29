package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.config;

import de.adorsys.opba.api.security.external.EnableOpenBankingRequestSigning;
import de.adorsys.opba.api.security.external.config.RequestSigningConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@EnableOpenBankingRequestSigning
@ConfigurationProperties("security")
@Validated
public class FintechRequestSigningTestConfig extends RequestSigningConfig {

}
