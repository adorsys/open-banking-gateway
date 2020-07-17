package de.adorsys.opba.protocol.facade.services.fintech.registrar;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPubKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.FintechOnlyKeyPairConfig;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.security.KeyPair;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class FintechRegistrar {

    private final EntityManager entityManager;
    private final FintechRepository fintechRepository;
    private final FintechSecureStorage fintechSecureStorage;
    private final FintechOnlyPubKeyRepository pubKeyRepository;
    private final FintechOnlyKeyPairConfig fintechOnlyKeyPairConfig;
    private final FintechOnlyEncryptionServiceProvider fintechOnlyEncryptionServiceProvider;

    @Transactional
    public Fintech registerFintech(String fintechId, Supplier<char[]> finTechPassword) {
        Fintech fintech = fintechRepository.save(Fintech.builder().globalId(fintechId).build());
        fintechSecureStorage.registerFintech(fintech, finTechPassword);
        for (int i = 0; i <  fintechOnlyKeyPairConfig.getPairCount(); ++i) {
            UUID id = UUID.randomUUID();
            KeyPair pair = fintechOnlyEncryptionServiceProvider.generateKeyPair();
            fintechSecureStorage.fintechOnlyPrvKeyToPrivate(id, pair.getPrivate(), fintech, finTechPassword);
            FintechPubKey pubKey = FintechPubKey.builder()
                    .prvKey(entityManager.find(FintechPrvKey.class, id))
                    .build();
            pubKey.setKey(pair.getPublic());
            pubKeyRepository.save(pubKey);
        }
        return fintech;
    }
}
