package de.adorsys.opba.protocol.facade.config.encryption;

import com.google.common.io.ByteStreams;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.types.api.actions.ReadRequest;
import de.adorsys.datasafe.types.api.actions.WriteRequest;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import de.adorsys.opba.protocol.facade.config.ApplicationTest;
import de.adorsys.opba.protocol.facade.config.encryption.impl.FintechDatasafe;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = ApplicationTest.class)
class DatasafeStorageTest {

    @Autowired
    private FintechDatasafe datasafe;

    @Autowired
    private FintechRepository fintechs;

    @Test
    @Transactional
    @SneakyThrows
    void test() {
        Fintech fintech = fintechs.save(Fintech.builder().build());
        UserIDAuth idAuth = new UserIDAuth(fintech.getId().toString(), "aaaa"::toCharArray);
        datasafe.registerFintech(idAuth);

        UUID inbox = UUID.randomUUID();
        try (OutputStream os = datasafe.inboxService().write(
                WriteRequest.forDefaultPublic(Collections.singleton(idAuth.getUserID()), "./" + inbox)
        )) {
            os.write("This is a test!".getBytes());
        }

        byte[] message;
        try (InputStream is = datasafe.inboxService().read(
                ReadRequest.forDefaultPrivate(idAuth, "./" + inbox)
        )) {
            message = ByteStreams.toByteArray(is);
        }

        UUID privateId = UUID.randomUUID();
        try (OutputStream os = datasafe.privateService().write(
                WriteRequest.forDefaultPrivate(idAuth, "./" + privateId)
        )) {
            os.write(message);
        }

        try (InputStream is = datasafe.privateService().read(
                ReadRequest.forDefaultPrivate(idAuth, "./" + privateId)
        )) {
            assertThat(is).hasContent("This is a test!");
        }
    }
}