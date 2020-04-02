package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechInbox;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrivate;
import de.adorsys.opba.db.repository.jpa.fintech.FintechInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPrivateRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.net.URI;

@Component
public class FintechDatasafeStorage extends BaseDatasafeDbStorageService {

    public FintechDatasafeStorage(
            DatasafeDataStorage<FintechPrivate> datasafePrivate,
            DatasafeDataStorage<FintechInbox> datasafeInbox,
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
    public static class FintechPrivateStorage extends DatasafeDataStorage<FintechPrivate> {

        public FintechPrivateStorage(FintechPrivateRepository privates, EntityManager em) {
            super(
                    privates,
                    (parent, id) -> FintechPrivate.builder().id(id).fintech(em.find(Fintech.class, parent)).build(),
                    FintechPrivate::getData,
                    FintechPrivate::setData
            );
        }
    }

    @Component
    public static class FintechInboxStorage extends DatasafeDataStorage<FintechInbox> {

        public FintechInboxStorage(FintechInboxRepository inboxes, EntityManager em) {
            super(
                    inboxes,
                    (parent, id) -> FintechInbox.builder().id(id).fintech(em.find(Fintech.class, parent)).build(),
                    FintechInbox::getData,
                    FintechInbox::setData
            );
        }
    }

    @Component
    public static class FintechKeystoreStorage extends DatasafeMetadataStorage {

        public FintechKeystoreStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getKeystore, Fintech::setKeystore);
        }
    }

    @Component
    public static class FintechPubKeysStorage extends DatasafeMetadataStorage {

        public FintechPubKeysStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getPubKeys, Fintech::setPubKeys);
        }
    }
}
