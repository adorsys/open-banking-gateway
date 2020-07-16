package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import com.google.common.collect.Iterables;
import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class AnonymousPsuPaymentAccess implements PaymentAccess {

    private final Bank aspsp;
    private final Fintech fintech;
    private final PsuEncryptionServiceProvider psuEncryption;
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

        return new ProtocolFacingPaymentImpl(newPayment, anonymousEncryptionServiceBasedOnRandomKeyFromFintech());
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

    private EncryptionService anonymousEncryptionServiceBasedOnRandomKeyFromFintech() {
        Collection<FintechPrvKey> keys = fintech.getFintechOnlyPrvKeys();
        // Expecting key amount to be small
        FintechPrvKey psuAspspKey = Iterables.get(keys, ThreadLocalRandom.current().nextInt(0, keys.size() - 1));
        return psuEncryption.forPublicKey(psuAspspKey.getId(), psuAspspKey.getPubKey().getKey());
    }
}
