package de.adorsys.opba.protocol.xs2a.service.validation;

import de.adorsys.opba.protocol.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Checker to validate that current context is validation-error free (all necessary parameters are in place)
 * and we can proceed with ASPSP API calls in same order as it was for validation.
 */
@Service("xs2aValidationErrors")
@RequiredArgsConstructor
public class Xs2aValidationErrors {

    public boolean isPresent(BaseContext ctx) {
        return !ctx.getViolations().isEmpty();
    }
}
