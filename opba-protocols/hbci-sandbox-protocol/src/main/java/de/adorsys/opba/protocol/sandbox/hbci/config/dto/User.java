package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Validated
public class User {

    @NotBlank
    private String login;

    @NotBlank
    private String realName;

    @NotBlank
    private String pin;

    @NotBlank
    private String tan;

    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> scaMethodsAvailable;

    @NotEmpty
    private List<@NotNull Account> accounts;

    @NotEmpty
    private List<@NotNull Transaction> transactions;

    public Account getDefaultAccount() {
        return accounts.get(0);
    }
}
