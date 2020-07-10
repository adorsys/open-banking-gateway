package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.AccountInformationRequestCommon;
import de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks.WiremockAccountInformationRequest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WiremockAccountInformationRequest.class)
@ComponentScan(basePackageClasses = {HbciJGivenConfig.class, AccountInformationRequestCommon.class})
@EnableJGiven
public class HbciJGivenConfig {
}
