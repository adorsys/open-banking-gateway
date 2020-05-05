package de.adorsys.opba.api.security.internal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "api.security.endpoint")
public class OperationTypeProperties {
    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> allowedPath;
}
