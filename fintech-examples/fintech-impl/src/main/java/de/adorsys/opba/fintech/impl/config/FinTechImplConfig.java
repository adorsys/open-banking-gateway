package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import de.adorsys.opba.fintech.impl.service.AuthorizeService;
import de.adorsys.opba.fintech.impl.service.RedirectHandlerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients(basePackages = "de.adorsys.opba.fintech.impl.tppclients")
@EnableJpaRepositories(basePackages = {"de.adorsys.opba.fintech.impl.database.repositories"})
@ComponentScan("de.adorsys.opba.fintech.impl")
@EntityScan("de.adorsys.opba.fintech.impl.database.entities")
public class FinTechImplConfig {

    @Value("${fintech-ui.nok-url}")
    private String notOkUrl;

    @Value("${fintech-ui.nok-url}")
    private String okUrl;

    @Value("${fintech-ui.nok-url}")
    private String exceptionUrl;

    @Bean
    public RedirectHandlerService redirectHandlerService(RedirectUrlRepository redirectUrlRepository, AuthorizeService authorizeService) {
        return new RedirectHandlerService(notOkUrl, okUrl, exceptionUrl, redirectUrlRepository, authorizeService);
    }
}
