package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.tpp.bankserach.api.resource.generated.TppBankSearchApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("de.adorsys.opba.fintech.impl")
@EnableConfigurationProperties({
        CookieConfigProperties.class
})
public class FinTechImplConfig {

    @Value("${tpp.url}")
    private String tppUrl;

    @Bean
    TppBankSearchApi tppBankSearchApi() {
        TppBankSearchApi tppBankSearchApi = new TppBankSearchApi();
        tppBankSearchApi.getApiClient().setBasePath(tppUrl);
        return tppBankSearchApi;
    }
}

