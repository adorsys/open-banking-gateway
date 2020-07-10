package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
public class BankSecurity {

    @NotNull
    private BpdAuthLevel bankParametersData;

    @NotNull
    private SensitiveAuthLevel accounts;

    @NotNull
    private SensitiveAuthLevel transactions;
}
