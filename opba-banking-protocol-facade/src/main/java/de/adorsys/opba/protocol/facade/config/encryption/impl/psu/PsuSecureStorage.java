package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.KeyGeneratorConfig;
import de.adorsys.opba.protocol.facade.config.encryption.datasafe.BaseDatasafeDbStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PsuSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final KeyGeneratorConfig.PsuSecretKeyGenerator keyGenerator;
    private final ObjectMapper mapper;

    public void registerPsu(Psu psu, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        psu.getUserIdAuth(password),
                        config.defaultPrivateTemplate(psu.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public SecretKey getOrCreateKeyFromPrivateForAspsp(Supplier<char[]> password, AuthSession session) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forDefaultPrivate(
                        session.getPsu().getUserIdAuth(password),
                        authId(session)
                )
        )) {
            SecretKeyContainer container = mapper.readValue(is, SecretKeyContainer.class);
            return new SecretKeySpec(container.getEncoded(), container.getAlgo());
        } catch (BaseDatasafeDbStorageService.DbStorageEntityNotFoundException ex) {
            return generateAndSaveAspspSecretKey(password, session);
        }
    }

    @NotNull
    private SecretKey generateAndSaveAspspSecretKey(Supplier<char[]> password, AuthSession session) throws IOException {
        SecretKey key = keyGenerator.generate();
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forDefaultPrivate(session.getPsu().getUserIdAuth(password), authId(session)))
        ) {
            os.write(mapper.writeValueAsBytes(new SecretKeyContainer(key.getAlgorithm(), key.getEncoded())));
        }
        return key;
    }

    @NotNull
    private String authId(AuthSession authSession) {
        return authSession.getId().toString();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SecretKeyContainer {

        private String algo;
        private byte[] encoded;
    }
}
