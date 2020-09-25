package de.adorsys.opba.fireflyexporter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URL;

@Data
@Configuration
@ConfigurationProperties(prefix = "firefly")
public class FireFlyConfig {

    @NotNull
    private URL fireFlyUrl;
}
