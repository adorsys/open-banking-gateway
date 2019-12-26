package de.adorsys.opba.core.protocol.service.xs2a.validation;

import de.adorsys.opba.core.protocol.domain.ValidationIssueException;
import de.adorsys.opba.core.protocol.domain.dto.ValidationIssue;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.opba.core.protocol.service.xs2a.context.ContextMode.REAL_CALLS;


@Service
@RequiredArgsConstructor
public class Xs2aValidator {

    private final Validator validator;

    public void validate(DelegateExecution exec, Object... dtosToValidate) {
        Set<ConstraintViolation<Object>> allErrors = new HashSet<>();

        for (Object value : dtosToValidate) {
            Set<ConstraintViolation<Object>> errors = validator.validate(value);
            allErrors.addAll(errors);
        }

        if (allErrors.isEmpty()) {
            return;
        }

        ContextUtil.getAndUpdateContext(
                exec,
                (BaseContext ctx) -> {
                    ctx.getViolations().addAll(allErrors.stream().map(this::toIssue).collect(Collectors.toSet()));
                    // Only when doing real calls validations cause termination of flow
                    // TODO: Those validation in real call should be propagated and handled
                    if (REAL_CALLS == ctx.getMode()) {
                        throw new ValidationIssueException();
                    }
                }
        );
    }

    private ValidationIssue toIssue(ConstraintViolation<Object> violation) {
        return ValidationIssue.builder()
                .code(violation.getMessageTemplate())
                .message(violation.getMessage())
                .build();
    }
}
