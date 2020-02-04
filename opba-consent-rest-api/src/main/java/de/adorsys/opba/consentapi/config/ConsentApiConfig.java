package de.adorsys.opba.consentapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "de.adorsys.opba.consentapi",
        "de.adorsys.opba.restapi.shared",
        "de.adorsys.opba.protocol.facade.services"
})
public class ConsentApiConfig {
}
