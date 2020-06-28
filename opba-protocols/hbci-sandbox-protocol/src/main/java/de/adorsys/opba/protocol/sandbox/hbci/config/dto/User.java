package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@Validated
public class User {

    @NotBlank
    private String login;

    @NotBlank
    private String realname;

    @NotBlank
    private String pin;

    @NotBlank
    private String tan;

    @NotEmpty
    private Map<@NotBlank String, @NotBlank String> scaMethodsAvailable;
}