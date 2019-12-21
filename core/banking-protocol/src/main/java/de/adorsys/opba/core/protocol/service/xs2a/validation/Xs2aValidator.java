package de.adorsys.opba.core.protocol.service.xs2a.validation;

import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

@Service
@RequiredArgsConstructor
public class Xs2aValidator {

    private final SmartValidator validator;

    public void validate(BaseContext context, Object... values) {
        //for ()
    }
}
