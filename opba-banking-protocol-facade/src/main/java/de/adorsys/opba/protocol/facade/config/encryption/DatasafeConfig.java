package de.adorsys.opba.protocol.facade.config.encryption;

import de.adorsys.datasafe.business.impl.service.DaggerDefaultDatasafeServices;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasafeConfig {

    @Bean
    public DefaultDatasafeServices datasafeServices() {
        return DaggerDefaultDatasafeServices.builder()
                .build();
    }
}
