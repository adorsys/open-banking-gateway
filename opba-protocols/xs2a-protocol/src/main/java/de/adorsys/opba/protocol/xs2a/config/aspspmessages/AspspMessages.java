package de.adorsys.opba.protocol.xs2a.config.aspspmessages;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * This class aggregates message templates that can be received from ASPSP.
 */
@Validated
@Data
@Configuration
@ConfigurationProperties("protocol.aspspmessages")
public class AspspMessages {

    /**
     * Represents message templates for the invalid credentials case. I.e. when user enters wrong password and
     * has more attempts - then the returned message from ASPSP satisfies this template.
     */
    @NotEmpty
    private Set<@NotBlank String> invalidCredentials;

    /**
     * Represents message templates for the invalid consent case. I.e. when user enters wrong IBAN to the consent
     * then the returned message from ASPSP satisfies this template.
     */
    @NotEmpty
    private Set<@NotBlank String> invalidConsent;
}
