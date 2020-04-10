package de.adorsys.opba.protocol.facade.config.encryption.impl.fintech;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FintechUserSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;

    public void registerFintechUser(FintechUser user, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        user.getUserIdAuth(password),
                        config.defaultPrivateTemplate(user.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public void toInboxForAuth(AuthSession authSession, String data) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(ImmutableSet.of(authSession.getFintechUser().getUserId()), authSession.getId().toString()))
        ) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SneakyThrows
    public String fromInboxForAuth(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(authSession.getFintechUser().getUserIdAuth(password), authSession.getId().toString()))
        ) {
            return new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
        }
    }
}
