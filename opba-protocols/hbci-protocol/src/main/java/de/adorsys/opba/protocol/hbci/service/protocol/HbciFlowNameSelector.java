package de.adorsys.opba.protocol.hbci.service.protocol;

import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Selects sub-process name to be executed using {@link HbciContext}. I.e.: calls different sub-processes for account
 * listing and transaction listing.
 */
@Service("hbciFlowNameSelector")
@RequiredArgsConstructor
public class HbciFlowNameSelector {

    /**
     * Sub-process name for current context (PSU/FinTech input) validation.
     */
    public String getNameForValidation(HbciContext ctx) {
        return actionName(ctx);
    }

    /**
     * Sub-process name for current context (PSU/FinTech input) execution (real calls to ASPSP API).
     */
    public String getNameForExecution(HbciContext ctx) {
        return actionName(ctx);
    }

    private String actionName(HbciContext ctx) {
        return ctx.getFlowByAction().get(ctx.getAction());
    }
}
