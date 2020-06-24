package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;

public interface ConditionProvider {

    boolean isMandatory(Xs2aContext context);
}
