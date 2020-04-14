package de.adorsys.opba.protocol.facade.config.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("facade")
public class FacadeAuthConfig {

    @NotNull
    private Redirect redirect;

    @NotNull
    private AuthorizationCookie cookie;

    @Data
    @Validated
    public static class Redirect {

        @NotBlank
        private String loginPage;

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

    @Data
    @Validated
    public static class AuthorizationCookie {

        @NotBlank
        private String pathTemplate;

        private String domain; // null allowed
    }
}
