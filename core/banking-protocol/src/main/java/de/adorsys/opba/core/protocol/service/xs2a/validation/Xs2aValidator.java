package de.adorsys.opba.core.protocol.service.xs2a.validation;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import de.adorsys.opba.core.protocol.domain.ValidationIssueException;
import de.adorsys.opba.core.protocol.domain.dto.ValidationIssue;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
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
        ValidationInfo info = findInfoOnViolation(violation);
        return ValidationIssue.builder()
                .uiCode(info.ui().value())
                .ctxCode(computeCtxCode(violation, info))
                .message(violation.getMessage())
                .build();
    }

    @SneakyThrows
    private ValidationInfo findInfoOnViolation(ConstraintViolation<Object> violation) {
        String name = Iterables.getLast(violation.getPropertyPath()).getName();
        Field fieldValue = ReflectionUtils.findField(violation.getLeafBean().getClass(), name);

        if (null == fieldValue) {
            throw new IllegalStateException("Validated field not found " + name);
        }

        if (!fieldValue.isAnnotationPresent(ValidationInfo.class)) {
            throw new IllegalStateException("Field is" + name + " not annotated with @ValidationInfo");
        }

        return fieldValue.getAnnotationsByType(ValidationInfo.class)[0];
    }

    private String computeCtxCode(ConstraintViolation<Object> violation, ValidationInfo info) {
        if (Strings.isNullOrEmpty(info.ctx().prefix())) {
            return info.ctx().value();
        }

        return info.ctx().prefix() + "." + violation.getPropertyPath().toString();
    }
}
