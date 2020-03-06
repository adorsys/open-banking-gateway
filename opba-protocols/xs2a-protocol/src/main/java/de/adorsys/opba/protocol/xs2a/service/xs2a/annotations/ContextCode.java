package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface ContextCode {

    /**
     * Direct path in context class or other type of field identifier. Is used for de-duplication and frontend rendering.
     * Also they can be used to read current context values (FUTURE dev).
     */
    FieldCode value() default FieldCode.NONE;

    /**
     * Whether the field belongs to AIS consent object. In such case its value/prefix is ignored.
     * Used for frontend rendering.
     */
    ScopeObject target() default ScopeObject.GENERAL;
}
