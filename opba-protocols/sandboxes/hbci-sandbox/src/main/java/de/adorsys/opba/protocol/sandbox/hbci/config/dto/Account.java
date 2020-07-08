package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;

@Data
@Validated
public class Account {

    @NotBlank
    private String number;

    @NotNull
    private Currency currency;

    @NotNull
    private BigDecimal balance;
}
