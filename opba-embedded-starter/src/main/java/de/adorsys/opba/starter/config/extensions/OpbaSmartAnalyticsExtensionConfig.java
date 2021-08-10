package de.adorsys.opba.starter.config.extensions;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// Might require setting 'spring.devtools.restart.enabled' to 'false' (system property) due to Classloader issues
@Configuration
@ConditionalOnClass(name = "de.adorsys.opba.analytics.smartanalytics.EnableAnalyticsSmartAnalytics")
@ComponentScan(basePackages = {"de.adorsys.opba.analytics.smartanalytics"})
public class OpbaSmartAnalyticsExtensionConfig {
}
