package de.adorsys.opba.protocol.xs2a.config.aspspmessages;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

import static de.adorsys.opba.protocol.xs2a.config.ConfigConst.XS2A_PROTOCOL_CONFIG_PREFIX;

/**
 * This class aggregates message templates that can be received from ASPSP.
 */
@Validated
@Data
@Configuration
@ConfigurationProperties(XS2A_PROTOCOL_CONFIG_PREFIX + "aspspmessages")
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

    /**
     * Represents message templates for the missing OAuth2 token case.
     * TODO: https://github.com/adorsys/open-banking-gateway/issues/976
     */
    @NotEmpty
    private Set<@NotBlank String> missingOauth2Token;
}
