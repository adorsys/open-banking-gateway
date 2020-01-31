package de.adorsys.opba.protocol.xs2a.service.validation;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("validationErrors")
@RequiredArgsConstructor
public class ValidationErrors {

    public boolean isPresent(BaseContext ctx) {
        return !ctx.getViolations().isEmpty();
    }
}
