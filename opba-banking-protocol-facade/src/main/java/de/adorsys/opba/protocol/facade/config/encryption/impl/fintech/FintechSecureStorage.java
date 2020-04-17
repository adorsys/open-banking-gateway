package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableSet;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechPsuAspspTuple;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FintechSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final EncryptionKeySerde serde;

    public void registerFintech(Fintech fintech, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        fintech.getUserIdAuth(password),
                        config.defaultPrivateTemplate(fintech.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    public void validatePassword(Fintech fintech, Supplier<char[]> password) {
        this.userProfile().updateReadKeyPassword(
                fintech.getUserIdAuth(password),
                fintech.getUserIdAuth(password).getReadKeyPassword()
        );
    }

    @SneakyThrows
    public void psuAspspKeyToInbox(AuthSession authSession, PrivateKey psuAspspKey) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(ImmutableSet.of(
                        authSession.getFintechUser().getFintech().getUserId()),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            serde.writePrivateKey(psuAspspKey, os);
        }
    }

    @SneakyThrows
    public PrivateKey psuAspspKeyFromInbox(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(
                        authSession.getFintechUser().getFintech().getUserIdAuth(password),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            return serde.readPrivateKey(is);
        }
    }

    @SneakyThrows
    public void psuAspspKeyToPrivate(AuthSession authSession, Fintech fintech, PrivateKey psuAspspKey, Supplier<char[]> password) {
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forDefaultPrivate(
                        fintech.getUserIdAuth(password),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            serde.writePrivateKey(psuAspspKey, os);
        }
    }

    @SneakyThrows
    public PrivateKey psuAspspKeyFromPrivate(ServiceSession session, Fintech fintech, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forDefaultPrivate(
                        fintech.getUserIdAuth(password),
                        new FintechPsuAspspTuple(session).toDatasafePathWithoutParent()))
        ) {
            return serde.readPrivateKey(is);
        }
    }
}
