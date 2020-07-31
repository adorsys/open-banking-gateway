package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PsuPaymentAccess implements PaymentAccess {

    private final Psu psu;
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
                .psu(psu)
                .aspsp(aspsp)
                .build();

        return new ProtocolFacingPaymentImpl(newPayment, encryptionService);
    }

    @Override
    public void save(ProtocolFacingPayment payment) {
        paymentRepository.save(((ProtocolFacingPaymentImpl) payment).getPayment());
    }

    @Override
    public void delete(ProtocolFacingPayment payment) {
        paymentRepository.delete(((ProtocolFacingPaymentImpl) payment).getPayment());
    }

    @Override
    public List<ProtocolFacingPayment> findByCurrentServiceSessionOrderByModifiedDesc() {
        return paymentRepository.findByServiceSessionIdOrderByModifiedAtDesc(serviceSession.getId())
                .stream()
                .map(it -> new ProtocolFacingPaymentImpl(it, encryptionService))
                .collect(Collectors.toList());
    }
}
