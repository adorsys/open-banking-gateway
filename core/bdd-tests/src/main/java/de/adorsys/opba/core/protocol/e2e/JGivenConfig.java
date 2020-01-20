package de.adorsys.opba.core.protocol.e2e;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = JGivenConfig.class)
@EnableJGiven
public class JGivenConfig {
}
