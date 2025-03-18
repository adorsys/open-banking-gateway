package de.adorsys.opba.protocol.hbci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

import static de.adorsys.opba.protocol.hbci.config.ConfigConst.HBCI_PROTOCOL_CONFIG_PREFIX;

@Data
@Validated
@Configuration
@ConfigurationProperties(HBCI_PROTOCOL_CONFIG_PREFIX + "sca")
public class HbciScaConfiguration {

    @NotEmpty
    Map<String, String> authenticationTypes;
}
