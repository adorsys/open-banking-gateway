package de.adorsys.opba.core.protocol.service.xs2a.validation;

import com.google.common.collect.Iterables;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class Xs2aValidator {

    private final Validator validator;

    public void validate(BaseContext context, Object... values) {
        Set<ConstraintViolation<Object>> allErrors = new HashSet<>();

        for (Object value : values) {
            Set<ConstraintViolation<Object>> errors = validator.validate(value);
            allErrors.addAll(errors);
        }

        // TODO signal based on context type
        if (!allErrors.isEmpty()) {
            throw new RuntimeException(Iterables.getFirst(allErrors, null).getMessage());
        }
    }
}
