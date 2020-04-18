package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
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

    public FintechDatasafeStorage(
            DatasafeDataStorage<FintechPsuAspspPrvKey> datasafePrivate,
            DatasafeDataStorage<FintechPsuAspspPrvKeyInbox> datasafeInbox,
            FintechKeystoreStorage datasafeKeystore,
            FintechPubKeysStorage datasafePub
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
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
    public static class FintechPsuAspspPrvKeyStorage extends DatasafeDataStorage<FintechPsuAspspPrvKey> {

        public FintechPsuAspspPrvKeyStorage(FintechPsuAspspPrvKeyRepository consents, TransactionOperations txOper, EntityManager em) {
            super(
                    consents,
                    path -> FintechPsuAspspTuple.buildFintechPrvKey(path, em),
                    path -> find(consents, path),
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

        public FintechPsuAspspPrvKeyInboxStorage(FintechPsuAspspPrvKeyInboxRepository inboxConsents, TransactionOperations txOper, EntityManager em) {
            super(
                    inboxConsents,
                    path -> FintechPsuAspspTuple.buildFintechInboxPrvKey(path, em),
                    path -> find(inboxConsents, path),
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
