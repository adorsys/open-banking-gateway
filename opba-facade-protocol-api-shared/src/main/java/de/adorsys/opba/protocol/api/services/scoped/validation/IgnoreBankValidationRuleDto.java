package de.adorsys.opba.protocol.api.services.scoped.validation;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class IgnoreBankValidationRuleDto {
    Long protocolId;
    String endpointClassCanonicalName;
    FieldCode validationCode;
    boolean forEmbedded;
    boolean forRedirect;
}
