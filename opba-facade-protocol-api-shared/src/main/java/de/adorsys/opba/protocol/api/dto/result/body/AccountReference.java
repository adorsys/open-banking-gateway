package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Account reference specification for list transactions result from protocol.
 */
@Value
@Builder
public class AccountReference {
    String iban;
    String bban;
    String pan;
    String maskedPan;
    String msisdn;
    String currency;
}
