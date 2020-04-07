package de.adorsys.opba.tppauthapi.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "tpp")
public class TppProperties {

    private String loginUrl;
    private String privateKey;
    private String publicKey;
    private String signAlgo;
    private Long keyValidity;
    private String jwsAlgo;
}
