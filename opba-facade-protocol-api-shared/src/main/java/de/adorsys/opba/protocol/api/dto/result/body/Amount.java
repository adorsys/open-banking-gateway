package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Builder;
import lombok.Value;

/**
 * Transaction amount specification object.
 */
@Value
@Builder
public class Amount {

    /**
     * Transaction currency.
     */
    private String currency;

    /**
     * Transaction amount in 0.00 format.
     */
    private String amount;
}
