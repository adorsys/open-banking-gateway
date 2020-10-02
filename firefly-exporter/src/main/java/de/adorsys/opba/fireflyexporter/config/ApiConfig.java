package de.adorsys.opba.fireflyexporter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
@Validated
@Configuration
@ConfigurationProperties("api")
public class ApiConfig {

    @NotNull
    private URI url;
}
