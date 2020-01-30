package de.adorsys.opba.protocol.xs2a.service.xs2a.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidationInfo {

    FrontendCode ui();
    ContextCode ctx();
}
