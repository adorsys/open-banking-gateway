package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.time.Instant;

/**
 * PSU payment representation view for protocol execution.
 */
public interface ProtocolFacingPayment {

    /**
     * Get payment ID that is used to identify this consent in ASPSP API calls.
     * In short, Payment ID that is returned by the ASPSP.
     */
    String getPaymentId();

    /**
     * Get the context of this payment to identify its scope, like the list of IBANs creditor-debtor.
     */
    String getPaymentContext();

    /**
     * Set payment ID that is used to identify this payment in ASPSP API calls.
     */
    void setPaymentId(String id);

    /**
     * Get the context of this payment to identify its scope.
     */
    void setPaymentContext(String context);

    Instant getCreatedAtTime();
}
