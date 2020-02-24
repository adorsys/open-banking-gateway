package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

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
    String value() default "";

    /**
     * Prefix how to reach field in context class that will be appended with validation error path.
     */
    String prefix() default "";

    /**
     * Whether the field belongs to AIS consent object. In such case its value/prefix is ignored.
     * Used for frontend rendering.
     */
    TargetObject target() default TargetObject.CONTEXT;
}
