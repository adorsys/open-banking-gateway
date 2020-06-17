package de.adorsys.opba.protocol.hbci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

import static de.adorsys.opba.protocol.hbci.config.ConfigConst.HBCI_PROTOCOL_CONFIG_PREFIX;

/**
 * Configures HBCI adapter.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(HBCI_PROTOCOL_CONFIG_PREFIX + "adapter")
public class HbciAdapterProperties {

    // Filled by defaults if missing
    private String hbciProduct;

    // Filled by defaults if missing
    private String hbciVersion;

    @Min(0)
    private long sysIdExpirationTimeMs;

    @Min(0)
    private long updExpirationTimeMs;
}
