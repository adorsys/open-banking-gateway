package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.PsuConsentEncryptionServiceProvider;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import de.adorsys.opba.protocol.facade.services.SecretKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PsuSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final PsuConsentEncryptionServiceProvider encryptionServiceProvider;
    private final SecretKeySerde serde;

    public void registerPsu(Psu psu, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        psu.getUserIdAuth(password),
                        config.defaultPrivateTemplate(psu.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public SecretKeyWithIv getOrCreateKeyFromPrivateForAspsp(Supplier<char[]> password, AuthSession session) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forDefaultPrivate(
                        session.getPsu().getUserIdAuth(password),
                        authId(session)
                )
        )) {
            return serde.read(is);
        } catch (BaseDatasafeDbStorageService.DbStorageEntityNotFoundException ex) {
            return generateAndSaveAspspSecretKey(password, session);
        }
    }

    @NotNull
    private SecretKeyWithIv generateAndSaveAspspSecretKey(Supplier<char[]> password, AuthSession session) throws IOException {
        SecretKeyWithIv key = encryptionServiceProvider.generateKey();
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forDefaultPrivate(session.getPsu().getUserIdAuth(password), authId(session)))
        ) {
            serde.write(key, os);
        }
        return key;
    }

    @NotNull
    private String authId(AuthSession authSession) {
        return authSession.getId().toString();
    }
}
