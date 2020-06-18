package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
@Getter
public class PaymentResult<SELF extends PaymentResult<SELF>> extends AccountInformationResult<SELF> {

    @Transactional
    public SELF open_banking_has_consent_for_max_musterman_payment() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }

    @SneakyThrows
    @Transactional
    public SELF open_banking_has_consent_for_anton_brueckner_payment() {
        assertThat(consents.findByServiceSessionId(UUID.fromString(serviceSessionId))).isNotEmpty();
        return self();
    }
}

