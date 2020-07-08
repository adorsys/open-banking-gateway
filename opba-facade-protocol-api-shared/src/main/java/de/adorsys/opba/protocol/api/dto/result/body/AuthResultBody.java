package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResultBody {

    private AisConsent aisConsent;

    private SinglePaymentBody singlePaymentBody;

    private String bankName;

    private String fintechName;
}
