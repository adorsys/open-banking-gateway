package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Selects sub-process name to be executed using {@link BaseContext}. I.e.: calls different sub-processes for account
 * listing and transaction listing.
 */
@Service("flowNameSelector")
@RequiredArgsConstructor
public class FlowNameSelector {

    /**
     * Sub-process name for current context (PSU/FinTech input) validation.
     */
    public String getNameForValidation(BaseContext ctx) {
        return actionName(ctx);
    }

    /**
     * Sub-process name for current context (PSU/FinTech input) execution (real calls to ASPSP API).
     */
    public String getNameForExecution(BaseContext ctx) {
        return actionName(ctx);
    }

    private String actionName(BaseContext ctx) {
        return ctx.getFlowByAction().get(ctx.getAction());
    }
}
