package de.adorsys.opba.tppauthapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.opba.tppauthapi",
        "de.adorsys.opba.restapi.shared"
})
public class TppAuthApiConfig {
}
