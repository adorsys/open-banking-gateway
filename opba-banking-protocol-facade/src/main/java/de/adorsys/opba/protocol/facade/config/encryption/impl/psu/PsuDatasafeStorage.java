package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.psu.PsuPrivate;
import de.adorsys.opba.db.repository.jpa.psu.PsuPrivateRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
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
