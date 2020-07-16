package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AnonymousPsuPaymentAccess implements PaymentAccess {

    private final Bank aspsp;
    private final EncryptionService encryptionService;
    private final ServiceSession serviceSession;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean isFinTechScope() {
        return false;
    }

    @Override
    public ProtocolFacingPayment createDoNotPersist() {
        Payment newPayment = Payment.builder()
                .serviceSession(serviceSession)
                .aspsp(aspsp)
                .build();

        return new ProtocolFacingPaymentImpl(newPayment, encryptionService);
    }

    @Override
    public void save(ProtocolFacingPayment payment) {
        paymentRepository.save(((ProtocolFacingPaymentImpl) payment).getPayment());
    }

    @Override
    public void delete(ProtocolFacingPayment consent) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public List<ProtocolFacingPayment> findByCurrentServiceSessionOrderByModifiedDesc() {
        throw new IllegalStateException("Not implemented");
    }
}
