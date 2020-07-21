package de.adorsys.opba.protocol.api.services.scoped.consent;

import java.util.List;

/**
 * Protocol facing access object for users payment(s).
 */
public interface PaymentAccess {

    /**
     * @return If payment is being accessed on behalf of FinTech.
     */
    boolean isFinTechScope();

    /**
     * Factory method for new payment,
     * @return new payment object that was not persisted and that can be modified.
     */
    ProtocolFacingPayment createDoNotPersist();

    /**
     * Save payment object to database.
     */
    void save(ProtocolFacingPayment consent);

    /**
     * Delete payment object from database.
     */
    void delete(ProtocolFacingPayment consent);

    /**
     * Available payments for current session execution.
     */
    List<ProtocolFacingPayment> findByCurrentServiceSessionOrderByModifiedDesc();

    /**
     * Available consent for current session execution with throwing exception
     */
    default ProtocolFacingPayment getFirstByCurrentSession() {
        List<ProtocolFacingPayment> payments = findByCurrentServiceSessionOrderByModifiedDesc();
        if (payments.isEmpty()) {
            throw new IllegalStateException("Context not found");
        }

        return payments.get(0);
    }
}
