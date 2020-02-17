package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("flowNameSelector")
@RequiredArgsConstructor
public class FlowNameSelector {

    public String getNameForValidation(BaseContext ctx) {
        return actionName(ctx);
    }

    public String getNameForExecution(BaseContext ctx) {
        return actionName(ctx);
    }

    private String actionName(BaseContext ctx) {
        return "xs2a-" + ctx.getAction().name().toLowerCase().replaceAll("_", "-");
    }
}
