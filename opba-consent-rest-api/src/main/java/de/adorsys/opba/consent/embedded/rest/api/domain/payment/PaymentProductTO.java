package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PaymentProductTO {
    SEPA("sepa-credit-transfers"),
    INSTANT_SEPA("instant-sepa-credit-transfers"),
    TARGET2("target-2-payments"),
    CROSS_BORDER("cross-border-credit-transfers");

    private String value;

    private static Map<String, PaymentProductTO> container = new HashMap<>();

    static {
        for (PaymentProductTO product : values()) {
            container.put(product.getValue(), product);
        }
    }

    PaymentProductTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<PaymentProductTO> getByValue(String value) {
        return Optional.ofNullable(container.get(value));
    }
}
