package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ValidationMode;

import java.util.Set;

public interface ExternalValidationModeDeclaration {

    default int priority() {
        return 0;
    }

    Set<FieldCode> appliesTo();

    boolean appliesToContext(Xs2aContext context);

    ValidationMode computeValidationMode(Xs2aContext context);
}
