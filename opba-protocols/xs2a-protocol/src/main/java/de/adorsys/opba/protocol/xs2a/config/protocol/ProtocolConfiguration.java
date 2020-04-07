package de.adorsys.opba.protocol.xs2a.config.protocol;

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
@ConfigurationProperties("protocol")
public class ProtocolConfiguration {

    @NotNull
    private Redirect redirect;

    @Data
    public static class Redirect {

        @NotNull
        private Consent consentAccounts;

        @NotNull
        private Parameters parameters;

        @NotBlank
        private String toAspsp;

        @Data
        public static class Consent {

            @NotBlank
            private String ok;

            @NotBlank
            private String nok;

            @NotBlank
            private String result;
        }

        @Data
        public static class Parameters {

            @Min(1)
            private int maxArraySize;

            @NotBlank
            private String provideMore;

            @NotBlank
            private String providePsuPassword;

            @NotBlank
            private String selectScaMethod;

            @NotBlank
            private String reportScaResult;

            @NotBlank
            private String providePsuIban;
        }
    }
}
