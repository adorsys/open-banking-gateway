package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Payment;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FintechPaymentAccess implements PaymentAccess {

    private final Fintech fintech;
    private final PsuConsentEncryptionServiceProvider encryptionService;
    private final FintechPsuAspspPrvKeyRepository keys;
    private final FintechSecureStorage fintechVault;
    private final PaymentRepository payments;
    private final EntityManager entityManager;
    private final UUID serviceSessionId;
    private final Supplier<char[]> fintechPassword;

    @Override
    public boolean isFinTechScope() {
        return true;
    }

    @Override
    public ProtocolFacingPayment createDoNotPersist() {
        throw new IllegalStateException("No PSU present - can't create payment");
    }

    @Override
    public void save(ProtocolFacingPayment consent) {
        throw new IllegalStateException("No PSU present - can't save payment");
    }

    @Override
    public void delete(ProtocolFacingPayment consent) {
        throw new IllegalStateException("No PSU present - can't delete payment");
    }

    @Override
    public List<ProtocolFacingPayment> findByCurrentServiceSessionOrderByModifiedDesc() {
        ServiceSession serviceSession = entityManager.find(ServiceSession.class, serviceSessionId);
        if (null == serviceSession || null == serviceSession.getAuthSession()) {
            return Collections.emptyList();
        }

        List<Payment> payments = this.payments.findByServiceSessionIdOrderByModifiedAtDesc(serviceSession.getId());
        if (payments.isEmpty()) {
            return Collections.emptyList();
        }

        EncryptionService psuEncryptionService = psuKeyBasedEncryptionService(serviceSession);

        return payments.stream()
                .map(it -> toProtocolFacingPayment(it, serviceSession, psuEncryptionService))
                .collect(Collectors.toList());
    }

    private ProtocolFacingPaymentImpl toProtocolFacingPayment(Payment payment, ServiceSession session, EncryptionService psuEncryptionService) {
        if (null == payment.getPsu()) {
            return anonymousPayment(payment);
        }

        return psuPayment(payment, session, psuEncryptionService);
    }

    private ProtocolFacingPaymentImpl psuPayment(Payment payment, ServiceSession session, EncryptionService psuEncryptionService) {
        if (!payment.getPsu().getId().equals(session.getAuthSession().getPsu().getId())) {
            throw new IllegalStateException(
                    String.format(
                            "Payment %s is for %d but session is for %d",
                            payment.getId().toString(),
                            payment.getPsu().getId(),
                            session.getAuthSession().getPsu().getId())
            );
        }

        return new ProtocolFacingPaymentImpl(payment, psuEncryptionService);
    }

    private ProtocolFacingPaymentImpl anonymousPayment(Payment payment) {
        return new ProtocolFacingPaymentImpl(payment, anonymousEncryptionService(payment.getFintechPubKey().getPrvKey()));
    }

    private EncryptionService psuKeyBasedEncryptionService(ServiceSession session) {
        Optional<FintechPsuAspspPrvKey> psuAspspPrivateKey = keys.findByFintechIdAndPsuIdAndAspspId(
                fintech.getId(),
                session.getAuthSession().getPsu().getId(),
                session.getAuthSession().getAction().getBankProfile().getBank().getId()
        );

        if (!psuAspspPrivateKey.isPresent()) {
            return null;
        }

        PrivateKey psuAspspKey = fintechVault.psuAspspKeyFromPrivate(session, fintech, fintechPassword);
        return encryptionService.forPrivateKey(psuAspspPrivateKey.get().getId(), psuAspspKey);
    }

    private EncryptionService anonymousEncryptionService(FintechPrvKey prvKey) {
        PrivateKey psuAspspKey = fintechVault.fintechPrvKeyFromPrivate(prvKey, fintech, fintechPassword);
        return encryptionService.forPrivateKey(prvKey.getId(), psuAspspKey);
    }
}
