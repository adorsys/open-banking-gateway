package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.consent;

import lombok.Data;


@Data
public class AccountReferenceLog {

    private String iban;
    private String bban;
    private String pan;
    private String maskedPan;
    private String msisdn;
    private String currency;

}
