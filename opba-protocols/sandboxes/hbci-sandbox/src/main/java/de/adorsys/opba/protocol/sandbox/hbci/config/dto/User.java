package de.adorsys.opba.protocol.sandbox.hbci.config.dto;

import com.google.common.base.Joiner;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

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

    /**
     * Please use ids from set 910,911,912,913,920,921,900 as defiled in
     * synch-bpd.json under path BPD.Params_4.TAN2StepPar6.ParTAN2Step.TAN2StepParams*
     * You can see their descriptions there too.
     */
    @NotEmpty
    private List<@NotBlank String> scaMethodsAvailable;

    @NotEmpty
    private List<@NotNull Account> accounts;

    @NotEmpty
    private List<@NotNull Transaction> transactions;

    public Account getDefaultAccount() {
        return accounts.get(0);
    }

    public String scaMethods() {
        return ":" + Joiner.on(":").join(scaMethodsAvailable);
    }
}
