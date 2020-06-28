package de.adorsys.opba.protocol.sandbox.hbci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Map;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "hbci")
public class HbciConfig {

    @NotEmpty
    private List<@NotNull User> users;

    @Data
    @Validated
    public static class User {

        @NotBlank
        private String login;

        @NotBlank
        private String pin;

        @NotBlank
        private String tan;

        @NotEmpty
        private List<@NotBlank String> accounts;

        @NotEmpty
        private Map<String, String> scaMethodList;

        private Map<@NotBlank String, Map<@NotBlank String, @NotNull Transaction>> transactions;

        @Data
        @Validated
        private static class Transaction {

            @NotBlank
            private String amount;

            @NotNull
            private Currency currency;

            @NotNull
            private LocalDateTime date;
        }
    }
}
