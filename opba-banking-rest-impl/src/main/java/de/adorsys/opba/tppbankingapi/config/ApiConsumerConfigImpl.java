package de.adorsys.opba.tppbankingapi.config;

import de.adorsys.opba.protocol.api.fintechspec.ApiConsumer;
import de.adorsys.opba.protocol.api.fintechspec.ApiConsumerConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static de.adorsys.opba.tppbankingapi.config.ConfigConst.BANKING_API_CONFIG_PREFIX;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = BANKING_API_CONFIG_PREFIX + "security")
public class ApiConsumerConfigImpl implements ApiConsumerConfig {

    @NotNull
    @NotEmpty
    private Map<@NotBlank String, @NotNull ApiConsumerImpl> consumers;

    @Data
    @Validated
    public static class ApiConsumerImpl implements ApiConsumer {
        @NotBlank
        private String name;

        @NotBlank
        private String publicKey;
    }
}
