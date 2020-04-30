package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Selects sub-process name to be executed using {@link Xs2aContext}. I.e.: calls different sub-processes for account
 * listing and transaction listing.
 */
@Service("xs2aFlowNameSelector")
@RequiredArgsConstructor
public class Xs2aFlowNameSelector {

    /**
     * Sub-process name for current context (PSU/FinTech input) validation.
     */
    public String getNameForValidation(Xs2aContext ctx) {
        return actionName(ctx);
    }

    /**
     * Sub-process name for current context (PSU/FinTech input) execution (real calls to ASPSP API).
     */
    public String getNameForExecution(Xs2aContext ctx) {
        return actionName(ctx);
    }

    private String actionName(Xs2aContext ctx) {
        return ctx.getFlowByAction().get(ctx.getAction());
    }
}
