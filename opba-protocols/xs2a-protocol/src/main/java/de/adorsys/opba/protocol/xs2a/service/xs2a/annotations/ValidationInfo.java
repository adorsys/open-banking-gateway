package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents the definition of {@link de.adorsys.opba.protocol.api.dto.ValidationIssue} that will be processed,
 * enriched and possibly shown by user. Should be used in conjunction with some validation, like this:
 *
 * <pre>
 * {@code
 * @ValidationInfo(ui = @FrontendCode(BOOLEAN), ctx = @ContextCode(value = RECURRING_INDICATOR, target = AIS_CONSENT))
 * @NotNull(message = "{no.ctx.recurringIndicator}")
 * private Boolean recurringIndicator;
 * }
 * </pre>
 * This snippet will cause {@link de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator} to throw an
 * {@link de.adorsys.opba.protocol.api.dto.ValidationIssue} in validation phase if `recurringIndicator` is null
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidationInfo {

    /**
     * Frontend facing description of the violation - i.e. input type to use.
     */
    FrontendCode ui();

    /**
     * Violating parameter code and its logical location in the form.
     */
    ContextCode ctx();
}
