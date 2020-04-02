package de.adorsys.opba.fintech.impl.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "tpp")
public class TppProperties {
    private String fintechID;
    private String serviceSessionPassword;
}
