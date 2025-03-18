package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.AccountAccessBodyValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Special validator for {@link de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.AisConsentInitiateBody.AccountAccessBody}
 * AIS consent specification that checks we can use the provided object to make call to ASPSP API.
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AccountAccessBodyValidator.class)
@Documented
public @interface ValidConsentBody {

    String message() default "{de.adorsys.opba.protocol.xs2a.service.xs2a.validation.AccountAccessBodyValidator.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
