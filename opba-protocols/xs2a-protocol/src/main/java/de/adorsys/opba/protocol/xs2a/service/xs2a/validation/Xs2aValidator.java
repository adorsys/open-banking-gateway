package de.adorsys.opba.protocol.xs2a.service.xs2a.validation;

import com.google.common.collect.Iterables;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.TypeCode;
import de.adorsys.opba.protocol.api.errors.ReturnableException;
import de.adorsys.opba.protocol.api.services.scoped.validation.FieldsToIgnoreLoader;
import de.adorsys.opba.protocol.api.services.scoped.validation.IgnoreValidationRule;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.domain.ValidationIssueException;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ExternalValidationModeDeclaration;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ValidationMode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode.REAL_CALLS;


/**
 * Key validation service that uses Hibernate-validator to check that required parameters are available before doing
 * ASPSP API call.
 * For {@link de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode#MOCK_REAL_CALLS} collects
 * all violations into the context to emit message that requires user to provide inputs that fix the violations.
 * For {@link de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode#REAL_CALLS} causes Runtime error
 * if API object fails the validation.
 */
@Slf4j
@Service
public class Xs2aValidator {

    private final Validator validator;
    private final Map<FieldCode, List<ExternalValidationModeDeclaration>> externalValidationMode;

    public Xs2aValidator(Validator validator, List<? extends ExternalValidationModeDeclaration> externalValidationMode) {
        this.validator = validator;

        this.externalValidationMode = new HashMap<>();
        for (ExternalValidationModeDeclaration it : externalValidationMode) {
            it.appliesTo().forEach(id -> this.externalValidationMode.computeIfAbsent(id, mapId -> new ArrayList<>()).add(it));
        }
    }

    /**
     * Validates that all parameters necessary to perform ASPSP API call is present.
     * In {@link de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode#MOCK_REAL_CALLS}
     * reports all violations into {@link BaseContext#getViolations()} (merging with already existing ones)
     *
     * @param exec           Current execution that will be updated with violations if present.
     * @param dtosToValidate ASPSP API call parameter objects to validate.
     */
    public <T> void validate(DelegateExecution exec, Xs2aContext context, Class<T> invokerClass, Object... dtosToValidate) {
        Set<ConstraintViolation<Object>> allErrors = new HashSet<>();

        FieldsToIgnoreLoader fieldsToIgnoreLoader = context.getRequestScoped().fieldsToIgnoreLoader();
        Map<FieldCode, IgnoreValidationRule> rulesMap = fieldsToIgnoreLoader.getIgnoreValidationRules(
                invokerClass,
                context.getActiveScaApproach()
        );
        for (Object value : dtosToValidate) {
            Set<ConstraintViolation<Object>> errors = validator.validate(value)
                    .stream()
                    .filter(f -> isFieldMandatory(f, context, rulesMap))
                    .collect(Collectors.toSet());
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
                        log.error("Fatal validation error for requestId={},sagaId={} - violations {}", ctx.getRequestId(), ctx.getSagaId(), allErrors);
                        throw new ValidationIssueException();
                    }
                }
        );
    }

    private ValidationIssue toIssue(ConstraintViolation<Object> violation) {
        ValidationInfo info = findInfoOnViolation(violation);

        if (info.ui().value() == TypeCode.PROHIBITED) {
            throw new ValidationIssueException("UI-prohibited field has invalid value: " + info.ctx().value());
        }

        return ValidationIssue.builder()
                .type(info.ui().value())
                .scope(info.ctx().target())
                .code(info.ctx().value())
                .captionMessage(violation.getMessage())
                .build();
    }

    private boolean isFieldMandatory(ConstraintViolation<Object> constraint, Xs2aContext context, Map<FieldCode, IgnoreValidationRule> rulesMap) {
        ValidationInfo info = findInfoOnViolation(constraint);
        ValidationMode computedValidationMode = tryOverrideValidationMode(info, context);
        return doNotIgnoreValidationError(info.ctx().value(), computedValidationMode, rulesMap);
    }

    private ValidationMode tryOverrideValidationMode(ValidationInfo info, Xs2aContext context) {
        List<? extends ExternalValidationModeDeclaration> externals = externalValidationMode.get(info.ctx().value());
        if (null == externals) {
            return info.validationMode();
        }

        return externals.stream()
                .filter(it -> it.appliesToContext(context))
                .max(Comparator.comparingInt(ExternalValidationModeDeclaration::priority))
                .map(it -> it.computeValidationMode(context))
                .orElse(info.validationMode());
    }

    private boolean doNotIgnoreValidationError(FieldCode fieldCode, ValidationMode mode, Map<FieldCode, IgnoreValidationRule> rulesMap) {
        boolean doNotIgnoreValidationError = mode == ValidationMode.MANDATORY;
        IgnoreValidationRule ignoreRule = rulesMap.get(fieldCode);
        if (ignoreRule != null) {
            doNotIgnoreValidationError = ignoreRule.applies();
        }

        return doNotIgnoreValidationError;
    }

    @SneakyThrows
    private ValidationInfo findInfoOnViolation(ConstraintViolation<Object> violation) {
        String name = Iterables.getLast(violation.getPropertyPath()).getName();
        Field fieldValue = ReflectionUtils.findField(violation.getLeafBean().getClass(), name);

        if (null == fieldValue) {
            throw new ReturnableException("Validated field not found " + name);
        }

        if (!fieldValue.isAnnotationPresent(ValidationInfo.class)) {
            throw new ReturnableException("Field " + name + " not annotated with @ValidationInfo");
        }

        return fieldValue.getAnnotationsByType(ValidationInfo.class)[0];
    }
}
