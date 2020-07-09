package de.adorsys.opba.fintech.server.config;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.server.feignmocks.TppBankSearchClientFeignMock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableFinTechImplConfig
@SpringBootApplication
public class TestConfig {

    @Bean
    public TppBankSearchClientFeignMock tppBankSearchClient() {
        return new TppBankSearchClientFeignMock();
    }

}