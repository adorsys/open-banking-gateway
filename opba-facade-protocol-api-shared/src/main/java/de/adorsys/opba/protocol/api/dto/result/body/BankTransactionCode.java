package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BankTransactionCode {
    private String code;
}
