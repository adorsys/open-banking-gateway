package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

public enum FrequencyCodeTO {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    EVERYTWOWEEKS("EveryTwoWeeks"),
    MONTHLY("Monthly"),
    EVERYTWOMONTHS("EveryTwoMonths"),
    QUARTERLY("Quarterly"),
    SEMIANNUAL("SemiAnnual"),
    ANNUAL("Annual");

    private String value;

    FrequencyCodeTO(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }


}
