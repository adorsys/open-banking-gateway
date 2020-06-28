package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Validated
public class Transaction {

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    @NotBlank
    private String amount;

    @NotNull
    private Currency currency;

    @NotNull
    private LocalDateTime date;
}
