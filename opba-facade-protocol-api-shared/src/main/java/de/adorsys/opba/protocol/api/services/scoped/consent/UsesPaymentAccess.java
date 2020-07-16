package de.adorsys.opba.protocol.api.services.scoped.consent;

/**
 * Protocol facing payment access object. Encapsulates payment operations into a single object. Is separated from
 * {@link ConsentAccess} as payments may use different keys for encryption (anonymous payment)
 */
public interface UsesPaymentAccess {

    /**
     * Get payment access for the protocol, so that protocol can create or modify consent without any knowledge
     * of payment persistence or encryption.
     */
    PaymentAccess paymentAccess();
}
