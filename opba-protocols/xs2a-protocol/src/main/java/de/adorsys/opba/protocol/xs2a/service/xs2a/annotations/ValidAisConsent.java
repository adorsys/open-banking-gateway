package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.AisConsentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = AisConsentValidator.class)
@Documented
public @interface ValidAisConsent {

    String message() default "{hibernate-validation.invalid-consent-mesage";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
