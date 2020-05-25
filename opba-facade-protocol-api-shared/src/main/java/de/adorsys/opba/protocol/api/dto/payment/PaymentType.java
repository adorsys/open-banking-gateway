package de.adorsys.opba.protocol.api.dto.payment;

public enum PaymentType {
    SINGLE("payments"),
    BULK("bulk-payments"),
    PERIODIC("periodic-payments");

    private String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
