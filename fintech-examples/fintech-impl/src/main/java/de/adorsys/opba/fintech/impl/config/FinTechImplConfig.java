package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
import de.adorsys.opba.tpp.bankserach.api.resource.generated.TppBankSearchApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
@ComponentScan("de.adorsys.opba.fintech.impl")
@EnableConfigurationProperties({
        CookieConfigProperties.class
})
public class FinTechImplConfig {

    @FeignClient(url = "${tpp.url}", name = "tppSearch")
    public interface TppBankSearchClient extends TppBankSearchApi {
    }
}

