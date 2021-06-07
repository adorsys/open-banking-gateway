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

@Data
@Validated
@Configuration
@ConfigurationProperties(FACADE_CONFIG_PREFIX + "urls")
public class FacadeAuthConfig {

    @NotNull
    private Redirect redirect;

    @NotNull
    private AuthorizationSessionKey authorizationSessionKey;

    @Data
    @Validated
    public static class Redirect {

        private ConsentLogin consentLogin;

        @Data
        @Validated
        public static class ConsentLogin {

            private Page page;

            @Data
            @Validated
            public static class Page {
                @NotBlank
                private String forAis;

                @NotBlank
                private String forPis;

                @NotBlank
                private String forPisAnonymous;

                @NotBlank
                private String forAisAnonymous;
            }

            @NotNull
            private PasswordConfig password;

            @Data
            @Validated
            public static class PasswordConfig {

                @Min(8)
                @SuppressWarnings("checkstyle:MagicNumber") // Magic minimal value - at least 8 bytes of entropy
                private int byteSize;
            }
        }
    }

    @Data
    @Validated
    public static class AuthorizationSessionKey {

        @NotNull
        private Cookie cookie;

        @Data
        @Validated
        public static class Cookie {

            @NotEmpty
            private List<@NotBlank String> pathTemplates;

            @NotNull
            private String redirectPathTemplate;

            private String domain; // null allowed
        }
    }
}
