package de.adorsys.opba.consent.embedded.rest.api.domain;

public enum ScaMethodTypeTO {
    EMAIL(false),
    MOBILE(false),
    CHIP_OTP(false),
    PHOTO_OTP(false),
    PUSH_OTP(false),
    SMS_OTP(false),
    APP_OTP(true);

    private final boolean decoupled;

    ScaMethodTypeTO(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public boolean isDecoupled() {
        return decoupled;
    }
}
