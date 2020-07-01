package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.TemplateBasedOperationHandler;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.interpolation.JsonTemplateInterpolation;
import org.springframework.stereotype.Service;

@Service("authenticatedSynchronization")
public class AuthenticatedSynchronization extends TemplateBasedOperationHandler {

    public AuthenticatedSynchronization(JsonTemplateInterpolation interpolation) {
        super(interpolation);
    }

    @Override
    protected String getTemplatePathAndUpdateCtxIfNeeded(SandboxContext context) {
        // SCA-synch
        if ("HKTAN".equals(context.getRequestDataRegex("TAN2Step6\\.SegHead\\.code"))
                && "HKIDN".equals(context.getRequestDataRegex("TAN2Step6\\.ordersegcode"))
                && "4".equals(context.getRequestDataRegex("TAN2Step6\\.process"))
        ) {
            return "response-templates/authenticated/synch-bpd-sca.json";
        }

        return "response-templates/authenticated/synch-bpd.json";
    }

    @Override
    protected Operation handledRequestType() {
        return Operation.SYNCH;
    }
}
