package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.datasafe.types.api.resource.StorageIdentifier;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
import de.adorsys.opba.db.repository.jpa.fintech.FintechOnlyPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPsuAspspPrvKeyRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechPsuAspspTuple;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Optional;

@Component
public class FintechDatasafeStorage extends BaseDatasafeDbStorageService {

    public static final String FINTECH_ONLY_PRV_KEYS_TABLE = "private-keys";
    public static final StorageIdentifier FINTECH_ONLY_KEYS_ID = new StorageIdentifier(FINTECH_ONLY_PRV_KEYS_TABLE);
    public static final String FINTECH_ONLY_PRV_KEYS = "db://" + FINTECH_ONLY_PRV_KEYS_TABLE + "/";

    public FintechDatasafeStorage(
            DatasafeDataStorage<FintechPrvKey> fintechOnlyPrvKeys,
            DatasafeDataStorage<FintechPsuAspspPrvKey> datasafePrivate,
            DatasafeDataStorage<FintechPsuAspspPrvKeyInbox> datasafeInbox,
            FintechKeystoreStorage datasafeKeystore,
            FintechPubKeysStorage datasafePub
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
                .put(FINTECH_ONLY_PRV_KEYS_TABLE, fintechOnlyPrvKeys)
                .put(tableId(PRIVATE_STORAGE), datasafePrivate)
                .put(tableId(INBOX_STORAGE), datasafeInbox)
                .put(tableId(KEYSTORE), datasafeKeystore)
                .put(tableId(PUB_KEYS), datasafePub)
                .build()
        );
    }

    private static String tableId(String value) {
        return URI.create(value).getHost();
    }

    @Component
    public static class FintechOnlyPrvKeyStorage extends DatasafeDataStorage<FintechPrvKey> {

        public FintechOnlyPrvKeyStorage(FintechOnlyPrvKeyRepository keys, TransactionOperations txOper, EntityManager em) {
            super(
                    keys,
                    path -> FintechOnlyPrvKeyTuple.buildFintechPrvKey(path, em),
                    path -> find(keys, path),
                    FintechPrvKey::getEncData,
                    FintechPrvKey::setEncData,
                    txOper
            );
        }

        private static Optional<FintechPrvKey> find(FintechOnlyPrvKeyRepository keys, String path) {
            FintechOnlyPrvKeyTuple psuAspspTuple = new FintechOnlyPrvKeyTuple(path);
            return keys.findByIdAndFintechId(
                    psuAspspTuple.getKeyId(),
                    psuAspspTuple.getFintechId()
            );
        }
    }

    @Component
    public static class FintechPsuAspspPrvKeyStorage extends DatasafeDataStorage<FintechPsuAspspPrvKey> {

        public FintechPsuAspspPrvKeyStorage(FintechPsuAspspPrvKeyRepository psuAspspKeysForConsentOrPayment, TransactionOperations txOper, EntityManager em) {
            super(
                    psuAspspKeysForConsentOrPayment,
                    path -> FintechPsuAspspTuple.buildFintechPrvKey(path, em),
                    path -> find(psuAspspKeysForConsentOrPayment, path),
                    FintechPsuAspspPrvKey::getEncData,
                    FintechPsuAspspPrvKey::setEncData,
                    txOper
            );
        }

        private static Optional<FintechPsuAspspPrvKey> find(FintechPsuAspspPrvKeyRepository consents, String path) {
            FintechPsuAspspTuple psuAspspTuple = new FintechPsuAspspTuple(path);
            return consents.findByFintechIdAndPsuIdAndAspspId(
                    psuAspspTuple.getFintechId(),
                    psuAspspTuple.getPsuId(),
                    psuAspspTuple.getAspspId()
            );
        }
    }

    @Component
    public static class FintechPsuAspspPrvKeyInboxStorage extends DatasafeDataStorage<FintechPsuAspspPrvKeyInbox> {

        public FintechPsuAspspPrvKeyInboxStorage(FintechPsuAspspPrvKeyInboxRepository inboxPsuAspspKeysForConsentOrPayment, TransactionOperations txOper, EntityManager em) {
            super(
                    inboxPsuAspspKeysForConsentOrPayment,
                    path -> FintechPsuAspspTuple.buildFintechInboxPrvKey(path, em),
                    path -> find(inboxPsuAspspKeysForConsentOrPayment, path),
                    FintechPsuAspspPrvKeyInbox::getEncData,
                    FintechPsuAspspPrvKeyInbox::setEncData,
                    txOper
            );
        }

        private static Optional<FintechPsuAspspPrvKeyInbox> find(FintechPsuAspspPrvKeyInboxRepository consents, String path) {
            FintechPsuAspspTuple psuAspspTuple = new FintechPsuAspspTuple(path);
            return consents.findByFintechIdAndPsuIdAndAspspId(
                    psuAspspTuple.getFintechId(),
                    psuAspspTuple.getPsuId(),
                    psuAspspTuple.getAspspId()
            );
        }
    }

    @Component
    public static class FintechKeystoreStorage extends DatasafeMetadataStorage<Fintech> {

        public FintechKeystoreStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getKeystore, Fintech::setKeystore);
        }
    }

    @Component
    public static class FintechPubKeysStorage extends DatasafeMetadataStorage<Fintech> {

        public FintechPubKeysStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getPubKeys, Fintech::setPubKeys);
        }
    }
}
