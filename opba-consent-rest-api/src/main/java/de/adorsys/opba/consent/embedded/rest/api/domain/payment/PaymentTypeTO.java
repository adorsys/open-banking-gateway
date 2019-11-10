package de.adorsys.opba.consent.embedded.rest.api.domain.payment;

public enum PaymentTypeTO {
    SINGLE {
        @Override
        public Class getPaymentClass() {
            return SinglePaymentTO.class;
        }
    },
    PERIODIC {
        @Override
        public Class getPaymentClass() {
            return PeriodicPaymentTO.class;
        }
    },
    BULK {
        @Override
        public Class getPaymentClass() {
            return BulkPaymentTO.class;
        }
    };

    public abstract Class getPaymentClass();
}
