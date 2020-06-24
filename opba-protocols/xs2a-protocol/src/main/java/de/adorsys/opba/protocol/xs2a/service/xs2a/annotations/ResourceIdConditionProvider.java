package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;

public class ResourceIdConditionProvider implements ConditionProvider {

    @Override
    public boolean isMandatory(Xs2aContext context) {
        return ProtocolAction.LIST_TRANSACTIONS.equals(context.getAction())
                && context.getServiceSessionId() != null;
    }
}
