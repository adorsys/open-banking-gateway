package de.adorsys.opba.protocol.api.dto.result.body;

import lombok.Data;

@Data
public class Amount {
    private String currency;
    private String amount;
}
