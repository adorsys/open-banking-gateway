package de.adorsys.opba.protocol.hbci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

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

    @NotNull
    @SuppressWarnings("checkstyle:MagicNumber") // Yes min BLZ 10000 is a magic number
    private List<@NotNull @Min(10000) Long> adorsysMockBanksBlz;

    @Min(0)
    private long sysIdExpirationTimeMs;

    @Min(0)
    private long updExpirationTimeMs;

    @Min(0)
    @SuppressWarnings("checkstyle:MagicNumber") // Default value
    private long bpdExpirationTimeMs = 300_000;
}
