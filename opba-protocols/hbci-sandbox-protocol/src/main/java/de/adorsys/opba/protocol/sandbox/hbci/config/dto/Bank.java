package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Validated
public class Bank {

    @NotBlank
    private String name;

    @NotBlank
    private String bic;

    @NotBlank
    private String blz;

    @NotNull
    private BankSecurity security;

    @NotNull
    private Set<String> users;
}
