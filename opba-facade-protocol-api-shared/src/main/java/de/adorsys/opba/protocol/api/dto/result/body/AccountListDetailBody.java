package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Account specification for list accounts result from protocol.
 */
@Value
@Builder
public class AccountListDetailBody {
    String resourceId;
    String externalResourceId;
    String iban;
    String bban;
    String msisdn;
    String currency;
    String name;
    String product;
    String cashAccountType;
    String status;
    String bic;
    String linkedAccounts;
    List<Balance> balances;
}
