package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class PaymentResult extends Stage<PaymentResult> {

    private static final int ANTON_BRUECKNER_BOOKED_TRANSACTIONS_COUNT = 8;
    private static final int MAX_MUSTERMAN_BOOKED_TRANSACTIONS_COUNT = 5;
    private static final String ANTON_BRUECKNER_IBAN = "DE80760700240271232400";
    private static final String MAX_MUSTERMAN_IBAN = "DE38760700240320465700";

    @Getter
    @ExpectedScenarioState
    private String responseContent;

    @ExpectedScenarioState
    protected String serviceSessionId;

    @ExpectedScenarioState
    protected String authSessionCookie;

    @Autowired
    private ConsentRepository consents;

    @ProvidedScenarioState
    protected String redirectCode;

    @Autowired
    private RequestSigningService requestSigningService;

}

