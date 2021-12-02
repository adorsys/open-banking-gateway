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

/**
 * Helper class that aggregates top-level FinTech related operations.
 */
@RequiredArgsConstructor
public class FintechSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final EncryptionKeySerde serde;

    /**
     * Registers FinTech in Datasafe and DB.
     * @param fintech new FinTech to register
     * @param password FinTechs' KeyStore password.
     */
    public void registerFintech(Fintech fintech, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        fintech.getUserIdAuth(password),
                        config.defaultPrivateTemplate(fintech.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    /**
     * Validates FinTechs' Datasafe/KeyStore password
     * @param fintech Target FinTech to check password for
     * @param password Password to validate
     */
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

    /**
     * Sends PSU/Fintech user private key to FinTechs' inbox at the consent confirmation.
     * @param authSession Authorization session for this PSU/Fintech user
     * @param psuKey Private Key to send to FinTechs' inbox
     */
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

    /**
     * Retrieves PSU/FinTech users' private key from FinTechs' inbox.
     * @param authSession Authorization session for this PSU/Fintech user
     * @param password Fintechs' Datasafe/KeyStore password
     * @return Keys to access PSU/FinTech users' key to read consent and its data
     */
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

    /**
     * Sends PSU/Fintechs' to FinTechs' private storage.
     * @param authSession Authorization session for this PSU/Fintech user
     * @param fintech FinTech to store to
     * @param psuKey Key to store
     * @param password FinTechs Datasafe/Keystore password
     */
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

    /**
     * Reads PSU/Fintechs' user private key from FinTechs' private storage.
     * @param session Service session with which consent is associated.
     * @param fintech Owner of the private storage.
     * @param password FinTechs' Datasafe/KeyStore password.
     * @return PSU/Fintechs' user consent protection key.
     */
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

    /**
     * Register Fintech private key in FinTechs' private Datasafe storage
     * @param id Key ID
     * @param key Key to store
     * @param fintech Owner of the key
     * @param password Keystore/Datasafe protection password
     */
    @SneakyThrows
    public void fintechOnlyPrvKeyToPrivate(UUID id, PubAndPrivKey key, Fintech fintech, Supplier<char[]> password) {
        try (OutputStream os = datasafeServices.privateService().write(
                WriteRequest.forPrivate(
                        fintech.getUserIdAuth(password),
                        FINTECH_ONLY_KEYS_ID,
                        new FintechOnlyPrvKeyTuple(fintech.getId(), id).toDatasafePathWithoutParent()))
        ) {
            serde.writeKey(key.getPublicKey(), key.getPrivateKey(), os);
        }
    }

    /**
     * Reads Fintechs' private key from its private Datasafe storage
     * @param prvKey Private key definition to tead
     * @param fintech Private key owner
     * @param password Keystore/Datasafe protection password
     * @return FinTechs' private key
     */
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
