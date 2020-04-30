package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Account specification for list accounts result from protocol.
 */
@Value
@Builder
public class AccountListDetailBody {
    private String resourceId;
    private String iban;
    private String bban;
    private String pan;
    private String maskedPan;
    private String msisdn;
    private String currency;
    private String name;
    private String product;
    private String cashAccountType;
    private String status;
    private String bic;
    private String linkedAccounts;
}
