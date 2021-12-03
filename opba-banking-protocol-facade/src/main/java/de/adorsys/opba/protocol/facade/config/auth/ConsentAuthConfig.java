package de.adorsys.opba.protocol.facade.config.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static de.adorsys.opba.protocol.facade.config.ConfigConst.FACADE_CONFIG_PREFIX;

/**
 * Configuration for OBG Login to Consent-UI authentication (applied on facade level)
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "urls")
public class ConsentAuthConfig {

    /**
     * Redirection for Consent-UI login configuration.
     */
    @NotNull
    private Redirect redirect;

    /**
     * Authorization session key cookie {@code Authorization-Session-Key} configuration.
     */
    @NotNull
    private AuthorizationSessionKey authorizationSessionKey;

    /**
     * Login configuration.
     */
    @Data
    @Validated
    public static class Redirect {

        /**
         * Consent UI login configuration.
         */
        private ConsentLogin consentLogin;

        /**
         * Allows to have single redirect code across session - improves application behavior in unstable network
         * conditions.
         */
        private boolean sessionWideRedirectCode;

        @Data
        @Validated
        public static class ConsentLogin {

            /**
             * Pages used for Consent UI login.
             */
            private Page page;

            @Data
            @Validated
            public static class Page {

                /**
                 * AIS PSU authenticated login page.
                 */
                @NotBlank
                private String forAis;

                /**
                 * PIS PSU authenticated login page.
                 */
                @NotBlank
                private String forPis;

                /**
                 * AIS anonymous PSU authenticated login page.
                 */
                @NotBlank
                private String forAisAnonymous;

                /**
                 * PIS anonymous PSU authenticated login page.
                 */
                @NotBlank
                private String forPisAnonymous;
            }

            /**
             * Password configuration for Fintech-user KeyStore.
             */
            @NotNull
            private PasswordConfig password;

            @Data
            @Validated
            public static class PasswordConfig {

                /**
                 * Minimum password bytes for Fintech-user KeyStore.
                 */
                @Min(8)
                @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 8 bytes of entropy
                private int byteSize;
            }
        }
    }

    /**
     * Configuration for {@code Authorization-Session-Key} cookie.
     */
    @Data
    @Validated
    public static class AuthorizationSessionKey {

        /**
         * Cookie configuration.
         */
        @NotNull
        private Cookie cookie;

        @Data
        @Validated
        public static class Cookie {

            /**
             * URL paths relative to {@code domain} where {@code Authorization-Session-Key} cookie applies during Consent-UI authorization.
             */
            @NotEmpty
            private List<@NotBlank String> pathTemplates;

            /**
             * URL paths relative to {@code domain} where {@code Authorization-Session-Key} cookie after redirecting back from ASPSP to OBG.
             */
            @NotNull
            private String redirectPathTemplate;

            /**
             * Domain for cookies.
             */
            private String domain; // null allowed
        }
    }
}
