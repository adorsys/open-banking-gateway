package de.adorsys.opba.consent.embedded.rest.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScaUserDataTO {
    private String id;
    @NotNull
    private ScaMethodTypeTO scaMethod;
    @NotNull
    private String methodValue;

    private boolean usesStaticTan;
    private String staticTan;
    private boolean decoupled;

    public boolean isDecoupled() {
        return scaMethod.isDecoupled();
    }
}
