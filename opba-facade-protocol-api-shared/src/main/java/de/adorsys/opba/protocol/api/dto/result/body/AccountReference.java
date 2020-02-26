package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

@Data
public class AccountReference {
    private String iban;
    private String bban;
    private String pan;
    private String maskedPan;
    private String msisdn;
    private String currency;
}
