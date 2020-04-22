package de.adorsys.opba.protocol.api.services.scoped.validation;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BankValidationRuleDto {
    Long protocolId;
    String endpointClass;
    String validationCode;
    boolean forEmbedded;
    boolean forRedirect;
}
