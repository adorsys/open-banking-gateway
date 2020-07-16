package de.adorsys.opba.protocol.facade.services.scoped.paymentaccess;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.PaymentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import de.adorsys.opba.protocol.api.services.scoped.consent.PaymentAccess;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
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
    private final FintechSecureStorage fintechVault;
    private final PsuConsentEncryptionServiceProvider psuEncryption;
    private final FintechPsuAspspPrvKeyRepository fintechPsuAspspPrvKeyRepository;
    private final PaymentRepository paymentRepository;

    public PaymentAccess paymentForPsuAndAspsp(Psu psu, Bank aspsp, ServiceSession session) {
        PsuAspspPrvKey prvKey = prvKeyRepository.findByPsuIdAndAspspId(psu.getId(), aspsp.getId())
                .orElseThrow(() -> new IllegalStateException("No public key for: " + psu.getId()));
        return new PsuPaymentAccess(psu, aspsp, psuEncryption.forPublicKey(prvKey.getId(), prvKey.getPubKey().getKey()), session, paymentRepository);
    }

    public PaymentAccess paymentAnonymousPsuAndAspsp(Bank aspsp, EncryptionService encryptionService, ServiceSession session) {
        return null;
    }

    public PaymentAccess paymentForFintech(Fintech fintech, ServiceSession session, Supplier<char[]> fintechPassword) {
        return null;
    }
}
