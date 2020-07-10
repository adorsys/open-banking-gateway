package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.config.encryption.impl.PairIdPsuAspspTuple;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PsuSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final PsuConsentEncryptionServiceProvider encryptionServiceProvider;
    private final EncryptionKeySerde serde;

    public void registerPsu(Psu psu, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        psu.getUserIdAuth(password),
                        config.defaultPrivateTemplate(psu.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public PrivateKey getOrCreateKeyFromPrivateForAspsp(Supplier<char[]> password, AuthSession session, BiConsumer<UUID, PublicKey> storePublicKeyIfNeeded) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forDefaultPrivate(
                        session.getPsu().getUserIdAuth(password),
                        new PairIdPsuAspspTuple(session).toDatasafePathWithoutPsuAndId()
                )
        )) {
            return serde.readPrivateKey(is);
        } catch (BaseDatasafeDbStorageService.DbStorageEntityNotFoundException ex) {
            return generateAndSaveAspspSecretKey(password, session, storePublicKeyIfNeeded);
        }
    }

    @SneakyThrows
    public PrivateKey createOneTimePrivateKey(Supplier<char[]> password, AuthSession session, BiConsumer<UUID, PublicKey> storePublicKeyIfNeeded) {
        return generateAndSaveAspspSecretKey(password, session, storePublicKeyIfNeeded);
    }

    @SneakyThrows
    private PrivateKey generateAndSaveAspspSecretKey(Supplier<char[]> password, AuthSession session, BiConsumer<UUID, PublicKey> storePublicKeyIfNeeded) {
        UUID keyId = UUID.randomUUID();
        KeyPair key = encryptionServiceProvider.generateKeyPair();
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forDefaultPrivate(
                        session.getPsu().getUserIdAuth(password),
                        new PairIdPsuAspspTuple(keyId, session).toDatasafePathWithoutPsu()))
        ) {
            serde.writePrivateKey(key.getPrivate(), os);
        }
        storePublicKeyIfNeeded.accept(keyId, key.getPublic());
        return key.getPrivate();
    }
}
