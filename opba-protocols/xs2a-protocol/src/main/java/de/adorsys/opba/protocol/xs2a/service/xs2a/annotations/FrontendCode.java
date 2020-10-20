package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import de.adorsys.opba.protocol.api.dto.codes.TypeCode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents field type that should be used as input for current violation on frontend UI (i.e. date, password, etc.).
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface FrontendCode {

    /**
     * Data/input type that is expected from frontend UI.
     */
    TypeCode value();
}
