package de.adorsys.opba.protocol.facade.config.encryption.impl.psu;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import de.adorsys.datasafe.business.impl.service.DefaultDatasafeServices;
import de.adorsys.datasafe.directory.api.config.DFSConfig;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PsuSecureStorage {

    @Delegate
    private final DefaultDatasafeServices datasafeServices;

    private final DFSConfig config;

    public void registerPsu(Psu psu, Supplier<char[]> password) {
        this.userProfile()
                .createDocumentKeystore(
                        psu.getUserIdAuth(password),
                        config.defaultPrivateTemplate(psu.getUserIdAuth(password)).buildPrivateProfile()
                );
    }

    @SneakyThrows
    public void toPsuInboxForAuth(AuthSession authSession, String data) {
        try (OutputStream os = datasafeServices.inboxService().write(
                WriteRequest.forDefaultPublic(ImmutableSet.of(authSession.getPsu().getUserId()), authSession.getId().toString()))
        ) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SneakyThrows
    public String fromPsuInboxForAuth(AuthSession authSession, Supplier<char[]> password) {
        try (InputStream is = datasafeServices.inboxService().read(
                ReadRequest.forDefaultPrivate(authSession.getPsu().getUserIdAuth(password), authSession.getId().toString()))
        ) {
            return new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
        }
    }
}
