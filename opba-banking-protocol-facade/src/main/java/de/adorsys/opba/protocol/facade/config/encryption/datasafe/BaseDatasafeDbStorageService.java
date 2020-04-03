package de.adorsys.opba.protocol.facade.config.encryption.datasafe;

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
import de.adorsys.datasafe.storage.api.StorageService;
import de.adorsys.datasafe.types.api.callback.ResourceWriteCallback;
import de.adorsys.datasafe.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe.types.api.resource.BasePrivateResource;
import de.adorsys.datasafe.types.api.resource.BasePublicResource;
import de.adorsys.datasafe.types.api.resource.ResolvedResource;
import de.adorsys.datasafe.types.api.resource.StorageIdentifier;
import de.adorsys.datasafe.types.api.resource.WithCallback;
import de.adorsys.datasafe.types.api.types.ReadStorePassword;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class BaseDatasafeDbStorageService implements StorageService {

    public static final String PRIVATE_STORAGE = "db://storage/";
    public static final String INBOX_STORAGE = "db://inbox/";
    public static final String KEYSTORE = "db://keystore/";
    public static final String PUB_KEYS = "db://pubkeys/";

    private final Map<String, StorageActions> handlers;

    @Override
    @Transactional
    public boolean objectExists(AbsoluteLocation absoluteLocation) {
        return handlers.get(deduceTable(absoluteLocation))
                .read(deduceId(absoluteLocation))
                .isPresent();
    }

    @Override
    @Transactional
    public Stream<AbsoluteLocation<ResolvedResource>> list(AbsoluteLocation absoluteLocation) {
        throw new IllegalStateException("Unsupported operation");
    }

    @Override
    @SneakyThrows
    @Transactional
    public InputStream read(AbsoluteLocation absoluteLocation) {
        return new ByteArrayInputStream(requireBytes(absoluteLocation));
    }

    @Override
    @Transactional
    public void remove(AbsoluteLocation absoluteLocation) {
        handlers.get(deduceTable(absoluteLocation)).delete(deduceId(absoluteLocation));
    }

    @Override
    @SneakyThrows
    @Transactional
    public OutputStream write(WithCallback<AbsoluteLocation, ? extends ResourceWriteCallback> withCallback) {
        return new SetAndSaveOnClose(
                deduceId(withCallback.getWrapped()),
                handlers.get(deduceTable(withCallback.getWrapped()))
        );
    }

    protected String deduceTable(AbsoluteLocation<?> path) {
        return path.location().getWrapped().getHost();
    }

    protected String deduceId(AbsoluteLocation<?> path) {
        return path.location().getWrapped().getPath().replaceAll("^/", "");
    }

    private byte[] requireBytes(AbsoluteLocation<?> location) {
        return handlers.get(deduceTable(location))
                .read(deduceId(location))
                .orElseThrow(() -> new IllegalArgumentException("Failed to find entity for " + location.location().toASCIIString()));
    }

    private static CreateUserPrivateProfile createUserPrivateProfile(UserIDAuth userIDAuth) {
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

    public interface StorageActions {

        void update(String id, byte[] data);
        Optional<byte[]> read(String id);
        void delete(String id);
    }

    @RequiredArgsConstructor
    public static class DbTableDFSConfig implements DFSConfig {

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
            return createUserPrivateProfile(userIDAuth);
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

    @RequiredArgsConstructor
    public static class DbTablePrivateOnlyDFSConfig implements DFSConfig {

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
            return createUserPrivateProfile(userIDAuth);
        }

        @Override
        public CreateUserPublicProfile defaultPublicTemplate(UserID userID) {
            throw new IllegalStateException("Not supported");
        }

        public UserPrivateProfile privateProfile(UserIDAuth userIDAuth) {
            String userId = userIDAuth.getUserID().getValue();

            return UserPrivateProfile.builder()
                    .privateStorage(Collections.singletonMap(StorageIdentifier.DEFAULT, BasePrivateResource.forAbsolutePrivate(PRIVATE_STORAGE + userId + "/")))
                    .keystore(BasePrivateResource.forAbsolutePrivate(KEYSTORE + userId))
                    .associatedResources(Collections.emptyList())
                    .publishPublicKeysTo(BasePublicResource.forAbsolutePublic("db://nowhere"))
                    .build();
        }
    }

    public static class DbTableUserRetrieval extends ProfileRetrievalServiceImpl {

        private final DFSConfig dfsConfig;

        public DbTableUserRetrieval(ProfileRetrievalServiceImplRuntimeDelegatable.ArgumentsCaptor captor) {
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

    @RequiredArgsConstructor
    private static class SetAndSaveOnClose extends OutputStream {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        private final String id;
        private final StorageActions actions;

        @Override
        public void write(int b) {
            os.write(b);
        }

        @Override
        public void write(@NotNull byte[] b) throws IOException {
            os.write(b);
        }

        @Override
        public void write(@NotNull byte[] b, int off, int len) {
            os.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            os.flush();
        }

        @Override
        public void close() throws IOException {
            os.close();
            actions.update(id, os.toByteArray());
        }
    }
}
