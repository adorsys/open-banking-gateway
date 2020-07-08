package de.adorsys.opba.protocol.facade.config;

import de.adorsys.opba.api.security.internal.EnableTokenBasedApiSecurity;
import de.adorsys.opba.api.security.internal.config.TppTokenProperties;
import de.adorsys.opba.db.config.EnableBankingPersistence;
import de.adorsys.opba.protocol.api.fintechspec.ApiConsumer;
import de.adorsys.opba.protocol.api.fintechspec.ApiConsumerConfig;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.Map;

import static de.adorsys.opba.api.security.internal.config.ConfigConst.API_CONFIG_PREFIX;

@EnableBankingPersistence
@SpringBootApplication(scanBasePackages = {
        "de.adorsys.opba.protocol.facade",
        "de.adorsys.opba.db"
})
public class ApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }

    @Configuration
    @EnableTokenBasedApiSecurity
    public static class TestConfig {

        @Validated
        @Configuration
        @ConfigurationProperties(API_CONFIG_PREFIX + "security")
        public class TppTokenPropertiesConfig extends TppTokenProperties {
        }

        @Data
        @Validated
        public class AdorsysFintechProfile implements ApiConsumer {
            private TppTokenPropertiesConfig tppTokenPropertiesConfig;

            @Override
            public String getName() {
                return "Adorsys Fintech";
            }

            @Override
            public String getPublicKey() {
                return tppTokenPropertiesConfig.getPublicKey();
            }
        }

        @Validated
        @Configuration
        public class ApiConsumerConfigImpl implements ApiConsumerConfig {

            @Override
            public Map<String, ? extends ApiConsumer> getConsumers() {
                return Collections.singletonMap("MY-SUPER-FINTECH-ID", new AdorsysFintechProfile());
            }
        }
    }
}
