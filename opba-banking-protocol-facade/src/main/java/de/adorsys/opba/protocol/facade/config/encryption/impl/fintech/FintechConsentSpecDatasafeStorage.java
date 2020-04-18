package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.FintechConsentSpec;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.repository.jpa.fintech.FintechConsentSpecRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechUserRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechUserAuthSessionTuple;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;

import javax.persistence.EntityManager;
import java.net.URI;

@Component
public class FintechConsentSpecDatasafeStorage extends BaseDatasafeDbStorageService {

    public FintechConsentSpecDatasafeStorage(
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

        public FintechConsentSpecStorage(FintechConsentSpecRepository specs, EntityManager em, TransactionOperations txOper) {
            super(
                    specs,
                    path -> FintechUserAuthSessionTuple.buildFintechConsentSpec(path, em),
                    path -> specs.findById(new FintechUserAuthSessionTuple(path).getAuthSessionId()),
                    FintechConsentSpec::getEncData,
                    FintechConsentSpec::setEncData,
                    txOper
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
