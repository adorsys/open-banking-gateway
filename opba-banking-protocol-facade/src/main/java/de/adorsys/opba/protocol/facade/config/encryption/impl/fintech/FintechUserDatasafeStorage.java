package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.FintechConsentSpec;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.net.URI;

@Component
public class FintechUserDatasafeStorage extends BaseDatasafeDbStorageService {

    public FintechUserDatasafeStorage(
            DatasafeDataStorage<FintechConsentSpec> datasafeInbox,
            FintechUserPubKeysStorage datasafeKeystore,
            FintechUserKeystoreStorage datasafePub
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
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
    public static class FintechConsentSpecStorage extends DatasafeDataStorage<FintechConsentSpec> {

        public FintechConsentSpecStorage(FintechUserInboxRepository inboxes, EntityManager em) {
            super(
                    inboxes,
                    (parent, id) -> FintechConsentSpec.builder().id(id).user(em.find(FintechUser.class, parent)).build(),
                    FintechConsentSpec::getData,
                    FintechConsentSpec::setData
            );
        }
    }

    @Component
    public static class FintechUserKeystoreStorage extends DatasafeMetadataStorage<FintechUser> {

        public FintechUserKeystoreStorage(FintechUserRepository fintechUsers) {
            super(fintechUsers, FintechUser::getKeystore, FintechUser::setKeystore);
        }
    }

    @Component
    public static class FintechUserPubKeysStorage extends DatasafeMetadataStorage<FintechUser> {

        public FintechUserPubKeysStorage(FintechUserRepository fintechUsers) {
            super(fintechUsers, FintechUser::getPubKeys, FintechUser::setPubKeys);
        }
    }
}
