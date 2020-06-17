package de.adorsys.opba.protocol.xs2a.tests.e2e.wiremock.mocks;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.protocol.xs2a.tests.e2e.stages.PaymentRequestCommon;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class WiremockPaymentRequest<SELF extends WiremockPaymentRequest<SELF>> extends PaymentRequestCommon<SELF> {
}
