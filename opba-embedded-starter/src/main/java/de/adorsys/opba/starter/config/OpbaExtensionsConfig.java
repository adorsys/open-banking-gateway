package de.adorsys.opba.starter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.opba.protocol.finapi.common",
        "de.adorsys.opba.protocol.finapi.webform"
})
public class OpbaExtensionsConfig {
}
