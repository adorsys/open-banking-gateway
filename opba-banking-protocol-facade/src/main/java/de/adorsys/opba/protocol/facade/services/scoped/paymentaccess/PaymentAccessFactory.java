package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.facade.config.encryption.PsuEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PaymentAccessFactory {

    private final EntityManager entityManager;
    private final PsuAspspPrvKeyRepository prvKeyRepository;
    private final FintechOnlyPubKeyRepository fintechPubKeys;
    private final FintechSecureStorage fintechVault;
    private final PsuEncryptionServiceProvider psuEncryption;
    private final FintechPsuAspspPrvKeyRepository fintechPsuAspspPrvKeyRepository;
    private final PaymentRepository paymentRepository;

    public PaymentAccess paymentForPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session) {
        PsuAspspPrvKey prvKey = prvKeyRepository.findByPsuIdAndAspspId(psu.getId(), aspsp.getId())
                .orElseThrow(() -> new IllegalStateException("No public key for: " + psu.getId()));
        return new PsuPaymentAccess(psu, aspsp, psuEncryption.forPublicKey(prvKey.getId(), prvKey.getPubKey().getKey()), session, paymentRepository);
    }

    public PaymentAccess paymentForAnonymousPsu(Fintech fintech, Bank aspsp, ServiceSession session) {
        return new AnonymousPsuPaymentAccess(aspsp, fintech, fintechPubKeys, psuEncryption, session, paymentRepository);
    }

    public PaymentAccess paymentForFintech(Fintech fintech, ServiceSession session, Supplier<char[]> fintechPassword) {
        return new FintechPaymentAccess(fintech, psuEncryption, fintechPsuAspspPrvKeyRepository, fintechVault, paymentRepository, entityManager, session.getId(), fintechPassword);
    }
}
