package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuPrivate;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuPrivateRepository;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeDataStorage;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.DatasafeMetadataStorage;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.net.URI;

@Component
public class PsuDatasafeStorage extends BaseDatasafeDbStorageService {

    public PsuDatasafeStorage(
            DatasafeDataStorage<PsuPrivate> datasafePrivate,
            PsuKeystoreStorage datasafeKeystore,
            PsuPubKeysStorage datasafePub
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
                .put(tableId(PRIVATE_STORAGE), datasafePrivate)
                .put(tableId(KEYSTORE), datasafeKeystore)
                .put(tableId(PUB_KEYS), datasafePub)
                .build()
        );
    }

    private static String tableId(String value) {
        return URI.create(value).getHost();
    }

    @Component
    public static class PsuPrivateStorage extends DatasafeDataStorage<PsuPrivate> {

        public PsuPrivateStorage(PsuPrivateRepository privates, EntityManager em) {
            super(
                    privates,
                    (parent, id) -> PsuPrivate.builder().id(id).psu(em.find(Psu.class, parent)).build(),
                    PsuPrivate::getData,
                    PsuPrivate::setData
            );
        }
    }

    @Component
    public static class PsuKeystoreStorage extends DatasafeMetadataStorage {

        public PsuKeystoreStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getKeystore, Fintech::setKeystore);
        }
    }

    @Component
    public static class PsuPubKeysStorage extends DatasafeMetadataStorage {

        public PsuPubKeysStorage(FintechRepository fintechs) {
            super(fintechs, Fintech::getPubKeys, Fintech::setPubKeys);
        }
    }
}
