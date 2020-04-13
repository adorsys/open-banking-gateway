package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableSet;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.services.SecretKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FintechSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final SecretKeySerde serde;

    public void registerFintech(Fintech fintech, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        fintech.getUserIdAuth(password),
                        config.defaultPrivateTemplate(fintech.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public void psuAspspKeyToInbox(AuthSession authSession, SecretKeyWithIv psuAspspKey) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(ImmutableSet.of(
                        authSession.getFintechUser().getFintech().getUserId()),
                        bankId(authSession)))
        ) {
            serde.writeAsBytes(psuAspspKey, os);
        }
    }

    @SneakyThrows
    public SecretKeyWithIv psuAspspKeyFromInbox(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(
                        authSession.getFintechUser().getFintech().getUserIdAuth(password),
                        bankId(authSession)))
        ) {
            return serde.readAsBytes(is);
        }
    }

    private String bankId(AuthSession authSession) {
        return authSession.getProtocol().getBankProfile().getBank().getUuid();
    }
}
