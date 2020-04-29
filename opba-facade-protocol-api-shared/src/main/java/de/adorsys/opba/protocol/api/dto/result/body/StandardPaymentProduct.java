package de.adorsys.opba.protocol.api.dto.result.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StandardPaymentProduct {
    SEPA("sepa-credit-transfers"),
    PAIN_SEPA("pain.001-sepa-credit-transfers"),
    TARGET2("TARGET2"),
    CROSS_BORDER("CROSS_BORDER");

    private final String value;

    StandardPaymentProduct(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StandardPaymentProduct fromValue(String text) {
        for (StandardPaymentProduct b : StandardPaymentProduct.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
