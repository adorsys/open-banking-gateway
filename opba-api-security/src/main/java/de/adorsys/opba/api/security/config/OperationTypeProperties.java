package de.adorsys.opba.api.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "security.endpoint")
public class OperationTypeProperties {
    @NotEmpty
    private ConcurrentHashMap<String, String> allowedPath;
}
