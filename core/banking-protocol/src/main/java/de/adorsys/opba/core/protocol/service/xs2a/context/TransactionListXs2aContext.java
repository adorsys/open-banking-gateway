package de.adorsys.opba.core.protocol.service.xs2a.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionListXs2aContext extends Xs2aContext {

    private String iban;
    private String resourceId;
    private String currency;
}
