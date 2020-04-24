package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Account reference specification for list transactions result from protocol.
 */
@Value
@Builder
public class AccountReference {
    private String iban;
    private String bban;
    private String pan;
    private String maskedPan;
    private String msisdn;
    private String currency;
}
