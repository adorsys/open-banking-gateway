package de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais;

import lombok.Data;
import lombok.EqualsAndHashCode;

// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountListXs2aContext extends Xs2aAisContext {

    // Optional consent-specific
    private Boolean withBalance;
}
