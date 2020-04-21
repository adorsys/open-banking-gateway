package de.adorsys.opba.protocol.api.services.scoped.validation;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BankValidationRuleDto {
    String endpointClass;
    String validationCode;
    boolean forEmbedded;
    boolean forRedirect;
}
