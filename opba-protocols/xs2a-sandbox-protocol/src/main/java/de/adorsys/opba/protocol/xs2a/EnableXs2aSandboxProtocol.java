package de.adorsys.opba.protocol.xs2a;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Note that in order to use EnableXs2aSandboxProtocol you need to import and apply to application profile
 * {@link EnableXs2aProtocol} as well or at least have xs2a-protocol jar on classpath.
 */
@Import(Xs2aProtocolConfiguration.class)
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableXs2aSandboxProtocol {
}
