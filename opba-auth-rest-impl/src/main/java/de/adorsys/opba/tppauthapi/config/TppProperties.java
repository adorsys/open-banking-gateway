package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "tpp")
public class TppProperties {

    private String loginUrl;
    private String privateKey;
    private String publicKey;
    private String signAlgo;
    @DurationUnit(ChronoUnit.DAYS)
    private Long keyValidityDays;
    private String jwsAlgo;
}
