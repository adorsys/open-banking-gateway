package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableSet;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechPsuAspspTuple;
import de.adorsys.opba.protocol.facade.dto.PubAndPrivKey;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechDatasafeStorage.FINTECH_ONLY_KEYS_ID;

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
        if (fintech.getFintechOnlyPrvKeys().isEmpty()) {
            throw new IllegalStateException("FinTech has no private keys");
        }

        var keys = fintech.getFintechOnlyPrvKeys().stream()
                .map(it -> this.fintechOnlyPrvKeyFromPrivate(it, fintech, password))
                .collect(Collectors.toList());

        if (keys.isEmpty()) {
            throw new IllegalStateException("Failed to extract FintTech keys");
        }
    }

    @SneakyThrows
    public void psuAspspKeyToInbox(AuthSession authSession, PubAndPrivKey psuKey) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(ImmutableSet.of(
                        authSession.getFintechUser().getFintech().getUserId()),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            serde.writeKey(psuKey.getPublicKey(), psuKey.getPrivateKey(), os);
        }
    }

    @SneakyThrows
    public PubAndPrivKey psuAspspKeyFromInbox(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(
                        authSession.getFintechUser().getFintech().getUserIdAuth(password),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            return serde.readKey(is);
        }
    }

    @SneakyThrows
    public void psuAspspKeyToPrivate(AuthSession authSession, Fintech fintech, PubAndPrivKey psuKey, Supplier<char[]> password) {
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forDefaultPrivate(
                        fintech.getUserIdAuth(password),
                        new FintechPsuAspspTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            serde.writeKey(psuKey.getPublicKey(), psuKey.getPrivateKey(), os);
        }
    }

    @SneakyThrows
    public PubAndPrivKey psuAspspKeyFromPrivate(ServiceSession session, Fintech fintech, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forDefaultPrivate(
                        fintech.getUserIdAuth(password),
                        new FintechPsuAspspTuple(session).toDatasafePathWithoutParent()))
        ) {
            return serde.readKey(is);
        }
    }

    @SneakyThrows
    public void fintechOnlyPrvKeyToPrivate(UUID id, PubAndPrivKey psuKey, Fintech fintech, Supplier<char[]> password) {
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forPrivate(
                        fintech.getUserIdAuth(password),
                        FINTECH_ONLY_KEYS_ID,
                        new FintechOnlyPrvKeyTuple(fintech.getId(), id).toDatasafePathWithoutParent()))
        ) {
            serde.writeKey(psuKey.getPublicKey(), psuKey.getPrivateKey(), os);
        }
    }

    @SneakyThrows
    public PubAndPrivKey fintechOnlyPrvKeyFromPrivate(FintechPrvKey prvKey, Fintech fintech, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.privateService().read(
                ReadRequest.forPrivate(
                        fintech.getUserIdAuth(password),
                        FINTECH_ONLY_KEYS_ID,
                        new FintechOnlyPrvKeyTuple(fintech.getId(), prvKey.getId()).toDatasafePathWithoutParent()))
        ) {
            return serde.readKey(is);
        }
    }
}
