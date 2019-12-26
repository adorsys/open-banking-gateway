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
                (BaseContext ctx) ->
                        ctx.getViolations().addAll(allErrors.stream().map(this::toIssue).collect(Collectors.toSet()))
        );

        // TODO - validation flow should group them
        throw new ValidationIssueException();
    }

    private ValidationIssue toIssue(ConstraintViolation<Object> violation) {
        return ValidationIssue.builder()
                .beanName(violation.getLeafBean().getClass().getName())
                .propertyPath(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .code(violation.getMessageTemplate())
                .build();
    }
}
