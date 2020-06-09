package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentRequestCommon;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WiremockPaymentRequest<SELF extends WiremockPaymentRequest<SELF>> extends PaymentRequestCommon<SELF> {

    @ExpectedScenarioState
    private WireMockServer wireMock;

    @Autowired
    private RequestSigningService requestSigningService;

}
