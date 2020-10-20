package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.ScopeObject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents the validation issue type and location in the context.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface ContextCode {

    /**
     * Code of the field that violates constraint.
     */
    FieldCode value() default FieldCode.NONE;

    /**
     * Logical location of the field that violates constraint within context. I.e. field PSU_ID may be shown in
     * general input form or in AIS consent object input form.
     */
    ScopeObject target() default ScopeObject.GENERAL;
}
