package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProtocolFacingPaymentImpl implements ProtocolFacingPayment {

    private final Payment payment;
    private final EncryptionService encryptionService;

    @Override
    public String getPaymentId() {
        return payment.getPaymentId(encryptionService);
    }

    @Override
    public String getPaymentContext() {
        return payment.getContext(encryptionService);
    }

    @Override
    public void setPaymentId(String id) {
        payment.setPaymentId(encryptionService, id);
    }

    @Override
    public void setPaymentContext(String context) {
        payment.setContext(encryptionService, context);
    }
}
