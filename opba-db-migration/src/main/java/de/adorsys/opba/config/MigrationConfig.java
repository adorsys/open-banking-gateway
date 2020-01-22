package de.adorsys.opba.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = de.adorsys.opba.config.hibernate.PrefixAndSnakeCasePhysicalNamingStrategy.class)
public class MigrationConfig {
}
