package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechUserAuthSessionTuple;
import de.adorsys.opba.protocol.facade.services.EncryptionKeySerde;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FintechConsentSpecSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;
    private final ObjectMapper mapper;

    public void registerFintechUser(FintechUser user, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        user.getUserIdAuth(password),
                        config.defaultPrivateTemplate(user.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public void toInboxForAuth(AuthSession authSession, FinTechUserInboxData data) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(
                        ImmutableSet.of(authSession.getFintechUser().getUserId()),
                        new FintechUserAuthSessionTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            os.write(mapper.writeValueAsBytes(data));
        }
    }

    @SneakyThrows
    public FinTechUserInboxData fromInboxForAuth(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(
                        authSession.getFintechUser().getUserIdAuth(password),
                        new FintechUserAuthSessionTuple(authSession).toDatasafePathWithoutParent()))
        ) {
            return mapper.readValue(is, FinTechUserInboxData.class);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinTechUserInboxData {

        @NonNull
        private URI afterPsuIdentifiedRedirectTo;

        @NonNull
        private EncryptionKeySerde.SecretKeyWithIvContainer protocolKey;

        private Object requirements;
    }
}
