package de.adorsys.opba.protocol.facade.services.fintech.registrar;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyKeyPairConfig;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.dto.PubAndPrivKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.security.KeyPair;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Registers FinTech in the DB, note that it must exist in application.yml configuration.
 */
@Service
@RequiredArgsConstructor
public class FintechRegistrar {

    private final EntityManager entityManager;
    private final FintechRepository fintechRepository;
    private final FintechSecureStorage fintechSecureStorage;
    private final FintechOnlyPubKeyRepository pubKeyRepository;
    private final FintechOnlyKeyPairConfig fintechOnlyKeyPairConfig;
    private final FintechOnlyEncryptionServiceProvider fintechOnlyEncryptionServiceProvider;

    /**
     * Register Fintech in the OBG database.
     * @param fintechId Fintech ID to register
     * @param finTechPassword Fintechs' KeyStore password
     * @return Newly created FinTech
     */
    @Transactional
    public Fintech registerFintech(String fintechId, Supplier<char[]> finTechPassword) {
        Fintech fintech = fintechRepository.save(Fintech.builder().globalId(fintechId).build());
        fintechSecureStorage.registerFintech(fintech, finTechPassword);
        for (int i = 0; i <  fintechOnlyKeyPairConfig.getPairCount(); ++i) {
            UUID id = UUID.randomUUID();
            KeyPair pair = fintechOnlyEncryptionServiceProvider.generateKeyPair();
            fintechSecureStorage.fintechOnlyPrvKeyToPrivate(id, new PubAndPrivKey(pair.getPublic(), pair.getPrivate()), fintech, finTechPassword);
            FintechPubKey pubKey = FintechPubKey.builder()
                    .prvKey(entityManager.find(FintechPrvKey.class, id))
                    .build();
            pubKey.setKey(pair.getPublic());
            pubKeyRepository.save(pubKey);
        }
        return fintech;
    }
}
