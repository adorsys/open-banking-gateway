package de.adorsys.opba.protocol.facade.config.encryption.impl;

import com.google.common.collect.ImmutableMap;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.directory.api.types.CreateUserPrivateProfile;
import de.adorsys.datasafe.directory.api.types.CreateUserPublicProfile;
import de.adorsys.datasafe.directory.api.types.UserPrivateProfile;
import de.adorsys.datasafe.directory.api.types.UserPublicProfile;
import de.adorsys.datasafe.directory.impl.profile.operations.actions.ProfileRetrievalServiceImpl;
import de.adorsys.datasafe.directory.impl.profile.operations.actions.ProfileRetrievalServiceImplRuntimeDelegatable;
import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.encrypiton.api.types.keystore.KeyStoreAuth;
import de.adorsys.datasafe.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe.types.api.resource.BasePrivateResource;
import de.adorsys.datasafe.types.api.resource.BasePublicResource;
import de.adorsys.datasafe.types.api.types.ReadStorePassword;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechInbox;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrivate;
import de.adorsys.opba.db.repository.jpa.fintech.FintechInboxRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechPrivateRepository;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class FintechStorage extends BaseStorage {

    public static final String PRIVATE_STORAGE = "db://storage/";
    public static final String INBOX_STORAGE = "db://inbox/";
    public static final String KEYSTORE = "db://keystore/";
    public static final String PUB_KEYS = "db://pubkeys/";

    public FintechStorage(
            EntityManager em,
            FintechRepository fintechs,
            FintechPrivateRepository privates,
            FintechInboxRepository inboxes
    ) {
        super(ImmutableMap.<String, StorageActions>builder()
                .put(
                        tableId(PRIVATE_STORAGE),
                        new FintechDataStorage<>(
                                privates,
                                (parent, id) -> FintechPrivate.builder().id(id).fintech(em.find(Fintech.class, parent)).build(),
                                FintechPrivate::getData,
                                FintechPrivate::setData
                        )
                )
                .put(
                        tableId(INBOX_STORAGE),
                        new FintechDataStorage<>(
                                inboxes,
                                (parent, id) -> FintechInbox.builder().id(id).fintech(em.find(Fintech.class, parent)).build(),
                                FintechInbox::getData,
                                FintechInbox::setData
                        )
                )
                .put(
                        tableId(KEYSTORE),
                        new FintechMetadataStorage(fintechs, Fintech::getKeystore, Fintech::setKeystore)
                )
                .put(
                        tableId(PUB_KEYS),
                        new FintechMetadataStorage(fintechs, Fintech::getPubKeys, Fintech::setPubKeys)
                )
                .build()
        );
    }

    private static String tableId(String value) {
        return URI.create(value).getHost();
    }

    @RequiredArgsConstructor
    private static class FintechMetadataStorage implements StorageActions {
        private final FintechRepository fintechs;
        private final Function<Fintech, byte[]> getData;
        private final BiConsumer<Fintech, byte[]> setData;

        @Override
        public BiConsumer<String, byte[]> getUpdate() {
            return (id, data) -> {
                Fintech fintech = fintechs.findById(Long.valueOf(id)).get();
                setData.accept(fintech, data);
                fintechs.save(fintech);
            };
        }

        @Override
        public Function<String, Optional<byte[]>> getRead() {
            return id -> fintechs.findById(Long.valueOf(id)).map(getData);
        }

        @Override
        public Consumer<String> getDelete() {
            throw new IllegalStateException("Not allowed");
        }
    }

    @RequiredArgsConstructor
    private static class FintechDataStorage<T> implements StorageActions {
        private final CrudRepository<T, UUID> repository;
        private final BiFunction<Long, UUID, T> factory;
        private final Function<T, byte[]> getData;
        private final BiConsumer<T, byte[]> setData;

        @Override
        public BiConsumer<String, byte[]> getUpdate() {
            return (id, data) -> {
                Optional<T> entry = repository.findById(uuid(id));
                if (entry.isPresent()) {
                    T toSave = entry.get();
                    setData.accept(toSave, data);
                    return;
                }

                T newEntry = factory.apply(parentId(id), uuid(id));
                setData.accept(newEntry, data);
                repository.save(newEntry);
            };
        }

        @Override
        public Function<String, Optional<byte[]>> getRead() {
            return id -> repository.findById(uuid(id)).map(getData);
        }

        private UUID uuid(String compositeId) {
            return UUID.fromString(compositeId.split("/")[1]);
        }

        private Long parentId(String compositeId) {
            return Long.parseLong(compositeId.split("/")[0]);
        }

        @Override
        public Consumer<String> getDelete() {
            throw new IllegalStateException("Not allowed");
        }
    }

    @RequiredArgsConstructor
    public static class FintechDFSConfig implements DFSConfig {

        private final String readKeystorePassword;

        @Override
        public KeyStoreAuth privateKeyStoreAuth(UserIDAuth userIDAuth) {
            return new KeyStoreAuth(
                    new ReadStorePassword(readKeystorePassword::toCharArray),
                    userIDAuth.getReadKeyPassword()
            );
        }

        @Override
        public AbsoluteLocation publicProfile(UserID userID) {
            throw new IllegalStateException("Not supported");
        }

        @Override
        public AbsoluteLocation privateProfile(UserID userID) {
            throw new IllegalStateException("Not supported");
        }

        @Override
        public CreateUserPrivateProfile defaultPrivateTemplate(UserIDAuth userIDAuth) {
            String userId = userIDAuth.getUserID().getValue();

            return CreateUserPrivateProfile.builder()
                    .id(userIDAuth)
                    .privateStorage(BasePrivateResource.forAbsolutePrivate(PRIVATE_STORAGE + userId + "/"))
                    .keystore(BasePrivateResource.forAbsolutePrivate(KEYSTORE + userId))
                    .inboxWithWriteAccess(BasePrivateResource.forAbsolutePrivate(INBOX_STORAGE + userId + "/"))
                    .publishPubKeysTo(BasePublicResource.forAbsolutePublic(PUB_KEYS + userId))
                    .associatedResources(Collections.emptyList())
                    .build();
        }

        @Override
        public CreateUserPublicProfile defaultPublicTemplate(UserID userID) {
            String userId = userID.getValue();

            return CreateUserPublicProfile.builder()
                    .id(userID)
                    .inbox(BasePublicResource.forAbsolutePublic(INBOX_STORAGE + userId + "/"))
                    .publicKeys(BasePublicResource.forAbsolutePublic(PUB_KEYS + userId))
                    .build();
        }
    }

    public static class FintechUserRetrieval extends ProfileRetrievalServiceImpl {

        private final DFSConfig dfsConfig;

        public FintechUserRetrieval(ProfileRetrievalServiceImplRuntimeDelegatable.ArgumentsCaptor captor) {
            super(null, null, null, null, null, null);
            this.dfsConfig = captor.getDfsConfig();
        }

        @Override
        public UserPublicProfile publicProfile(UserID ofUser) {
            return dfsConfig.defaultPublicTemplate(ofUser).buildPublicProfile();
        }

        @Override
        public UserPrivateProfile privateProfile(UserIDAuth ofUser) {
            return dfsConfig.defaultPrivateTemplate(ofUser).buildPrivateProfile();
        }

        @Override
        public boolean userExists(UserID ofUser) {
            return false;
        }
    }
}
