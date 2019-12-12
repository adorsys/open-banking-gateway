package de.adorsys.opba.consent.rest.api.domain;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
