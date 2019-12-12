package de.adorsys.opba.consent.rest.api.domain;

public enum FrequencyCodeTO {
  DAILY("Daily"), WEEKLY("Weekly"), EVERYTWOWEEKS("EveryTwoWeeks"), MONTHLY("Monthly"),
  EVERYTWOMONTHS("EveryTwoMonths"), QUARTERLY("Quarterly"), SEMIANNUAL("SemiAnnual"), ANNUAL("Annual");

  private String value;

  FrequencyCodeTO(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
