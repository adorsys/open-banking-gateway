package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
import de.adorsys.opba.db.repository.jpa.fintech.FintechConsentInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechConsentRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import org.springframework.stereotype.Component;

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
    public static class FintechConsentStorage extends DatasafeDataStorage<FintechPsuAspspPrvKey> {

        public FintechConsentStorage(FintechConsentRepository privates, EntityManager em) {
            super(
                    privates,
                    (parent, id) -> null, //FintechPsuAspspPrvKey.builder().consent(em.find(Consent.class, id)).fintech(em.find(Fintech.class, parent)).build(),
                    FintechPsuAspspPrvKey::getEncData,
                    FintechPsuAspspPrvKey::setEncData
            );
        }

        @Override
        protected Optional<FintechPsuAspspPrvKey> find(String id) {
            return Optional.empty(); //((FintechConsentRepository) repository).findByFintechIdAndConsentId(parentId(id), uuid(id));
        }
    }

    @Component
    public static class FintechConsentInboxStorage extends DatasafeDataStorage<FintechPsuAspspPrvKeyInbox> {

        public FintechConsentInboxStorage(FintechConsentInboxRepository inboxes, EntityManager em) {
            super(
                    inboxes,
                    (parent, id) -> FintechPsuAspspPrvKeyInbox.builder().id(id).fintech(em.find(Fintech.class, parent)).build(),
                    FintechPsuAspspPrvKeyInbox::getEncData,
                    FintechPsuAspspPrvKeyInbox::setEncData
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
