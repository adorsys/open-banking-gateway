package de.adorsys.opba.protocol.facade.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class SecretKeySerde {

    private final ObjectMapper mapper;

    @SneakyThrows
    public String asString(SecretKeyWithIv secretKeyWithIv) {
        return mapper.writeValueAsString(
                new SecretKeyWithIvContainer(
                        secretKeyWithIv.getSecretKey().getAlgorithm(),
                        secretKeyWithIv.getSecretKey().getEncoded(),
                        secretKeyWithIv.getIv())
        );
    }

    @SneakyThrows
    public SecretKeyWithIv fromString(String fromString) {
        SecretKeyWithIvContainer container = mapper.readValue(fromString, SecretKeyWithIvContainer.class);
        return new SecretKeyWithIv(
                container.getIv(),
                new SecretKeySpec(container.getEncoded(), container.getAlgo())
        );
    }

    @SneakyThrows
    public void writeAsBytes(SecretKeyWithIv value, OutputStream os) {
        mapper.writerFor(SecretKeyWithIv.class).writeValue(os, value);
    }

    @SneakyThrows
    public SecretKeyWithIv readAsBytes(InputStream is) {
        SecretKeyWithIvContainer container = mapper.readValue(is, SecretKeyWithIvContainer.class);
        return new SecretKeyWithIv(
                container.getIv(),
                new SecretKeySpec(container.getEncoded(), container.getAlgo())
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SecretKeyWithIvContainer {

        private String algo;
        private byte[] encoded;
        private byte[] iv;
    }
}
