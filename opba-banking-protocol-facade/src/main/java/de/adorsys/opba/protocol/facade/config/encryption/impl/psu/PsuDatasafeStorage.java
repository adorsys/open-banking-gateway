package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import de.adorsys.opba.db.repository.jpa.psu.PsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.PairIdPsuAspspTuple;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Optional;

@Component
public class PsuDatasafeStorage extends BaseDatasafeDbStorageService {

    public PsuDatasafeStorage(
            DatasafeDataStorage<PsuAspspPrvKey> datasafePrivate,
            PsuKeystoreStorage datasafeKeystore,
            PsuPubKeysStorage datasafePubKeys
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
                .put(tableId(PRIVATE_STORAGE), datasafePrivate)
                .put(tableId(KEYSTORE), datasafeKeystore)
                .put(tableId(PUB_KEYS), datasafePubKeys)
                .build()
        );
    }

    private static String tableId(String value) {
        return URI.create(value).getHost();
    }

    @Component
    public static class PsuAspspPrvKeyStorage extends DatasafeDataStorage<PsuAspspPrvKey> {

        public PsuAspspPrvKeyStorage(PsuAspspPrvKeyRepository privateKeys, EntityManager em, TransactionOperations txOper) {
            super(
                    privateKeys,
                    path -> PairIdPsuAspspTuple.buildPrvKey(path, em),
                    path -> find(privateKeys, path),
                    PsuAspspPrvKey::getEncData,
                    PsuAspspPrvKey::setEncData,
                    txOper
            );
        }

        private static Optional<PsuAspspPrvKey> find(PsuAspspPrvKeyRepository consents, String path) {
            PairIdPsuAspspTuple psuAspspTuple = new PairIdPsuAspspTuple(path);
            return consents.findByPsuIdAndAspspId(
                    psuAspspTuple.getPsuId(),
                    psuAspspTuple.getAspspId()
            );
        }
    }

    @Component
    public static class PsuKeystoreStorage extends DatasafeMetadataStorage<Psu> {

        public PsuKeystoreStorage(PsuRepository psus) {
            super(psus, Psu::getKeystore, Psu::setKeystore);
        }
    }

    @Component
    public static class PsuPubKeysStorage extends DatasafeMetadataStorage<Psu> {

        public PsuPubKeysStorage(PsuRepository psus) {
            super(psus, Psu::getPubKeys, Psu::setPubKeys);
        }
    }
}
