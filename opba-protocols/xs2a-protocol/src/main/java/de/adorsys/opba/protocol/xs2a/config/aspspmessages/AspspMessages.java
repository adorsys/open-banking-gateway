package de.adorsys.opba.protocol.xs2a.config.aspspmessages;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Validated
@Data
@Configuration
@ConfigurationProperties("protocol.aspspmessages")
public class AspspMessages {

    @NotEmpty
    private Set<@NotBlank String> invalidCredentials;

    @NotEmpty
    private Set<@NotBlank String> invalidConsent;
}
