package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.io.ByteStreams;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.db.repository.jpa.psu.PsuRepository;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.config.encryption.impl.fintech.FintechSecureStorage;
import de.adorsys.opba.protocol.facade.config.encryption.impl.psu.PsuSecureStorage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

import static de.adorsys.datasafe.types.api.actions.WriteRequest.forDefaultPrivate;
import static de.adorsys.datasafe.types.api.actions.WriteRequest.forDefaultPublic;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest(classes = ApplicationTest.class)
class DatasafeStorageIntegrationCheckTest {

    private static final Supplier<char[]> PASSWORD = "A very secret password"::toCharArray;
    private static final String DATA_TO_STORE = "This is a test!";

    @Autowired
    private FintechSecureStorage fintechSecureStorage;

    @Autowired
    private FintechRepository fintechs;

    @Autowired
    private PsuSecureStorage psuSecureStorage;

    @Autowired
    private PsuRepository psus;

    @Test
    @SneakyThrows
    void testFintechDatasafeIntegration() {
        Fintech fintech = fintechs.save(Fintech.builder().build());
        UserIDAuth idAuth = new UserIDAuth(fintech.getId().toString(), PASSWORD);
        fintechSecureStorage.registerFintech(idAuth);

        String inboxFile = UUID.randomUUID().toString();
        try (OutputStream os = fintechSecureStorage.inboxService().write(forDefaultPublic(Collections.singleton(idAuth.getUserID()), inboxFile))) {
            os.write(DATA_TO_STORE.getBytes(StandardCharsets.UTF_8));
        }

        byte[] message;
        try (InputStream is = fintechSecureStorage.inboxService().read(ReadRequest.forDefaultPrivate(idAuth, inboxFile))) {
            message = ByteStreams.toByteArray(is);
        }

        String privateFile = UUID.randomUUID().toString();
        try (OutputStream os = fintechSecureStorage.privateService().write(forDefaultPrivate(idAuth, privateFile))) {
            os.write(message);
        }

        try (InputStream is = fintechSecureStorage.privateService().read(ReadRequest.forDefaultPrivate(idAuth, privateFile))) {
            assertThat(is).hasContent(DATA_TO_STORE);
        }
    }

    @Test
    @SneakyThrows
    void testPsuDatasafeIntegration() {
        Psu psu = psus.save(Psu.builder().build());
        UserIDAuth idAuth = new UserIDAuth(psu.getId().toString(), PASSWORD);
        psuSecureStorage.registerPsu(idAuth);

        String privateFile = UUID.randomUUID().toString();
        try (OutputStream os = psuSecureStorage.privateService().write(forDefaultPrivate(idAuth, privateFile))) {
            os.write(DATA_TO_STORE.getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream is = psuSecureStorage.privateService().read(ReadRequest.forDefaultPrivate(idAuth, privateFile))) {
            assertThat(is).hasContent(DATA_TO_STORE);
        }
    }
}