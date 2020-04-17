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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
@RequiredArgsConstructor
public class EncryptionKeySerde {

    private static final String PKCS_8 = "PKCS#8";

    private final ObjectMapper mapper;

    @SneakyThrows
    public String asString(SecretKeyWithIv secretKeyWithIv) {
        return mapper.writeValueAsString(new SecretKeyWithIvContainer(secretKeyWithIv));
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
    public void write(SecretKeyWithIv value, OutputStream os) {
        // Mapper may choose to close the stream if using stream interface, we don't want this
        // as objects are small - this is ok.
        os.write(mapper.writeValueAsBytes(new SecretKeyWithIvContainer(value)));
    }

    @SneakyThrows
    public SecretKeyWithIv read(InputStream is) {
        SecretKeyWithIvContainer container = mapper.readValue(is, SecretKeyWithIvContainer.class);
        return new SecretKeyWithIv(
                container.getIv(),
                new SecretKeySpec(container.getEncoded(), container.getAlgo())
        );
    }

    @SneakyThrows
    public void writePrivateKey(PrivateKey value, OutputStream os) {
        // Mapper may choose to close the stream if using stream interface, we don't want this
        // as objects are small - this is ok.
        os.write(mapper.writeValueAsBytes(new PrivateKeyContainer(value)));
    }

    @SneakyThrows
    public PrivateKey readPrivateKey(InputStream is) {
        PrivateKeyContainer container = mapper.readValue(is, PrivateKeyContainer.class);
        if (!PKCS_8.equals(container.getFormat())) {
            throw new IllegalArgumentException("Bad key format");
        }
        KeyFactory factory = KeyFactory.getInstance(container.getAlgo());
        return factory.generatePrivate(new PKCS8EncodedKeySpec(container.getEncoded()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecretKeyWithIvContainer {

        private String algo;
        private byte[] encoded;
        private byte[] iv;

        public SecretKeyWithIvContainer(SecretKeyWithIv key) {
            this.algo = key.getSecretKey().getAlgorithm();
            this.encoded = key.getSecretKey().getEncoded();
            this.iv = key.getIv();
        }

        public SecretKeyWithIv asKey() {
            return new SecretKeyWithIv(
                    this.iv,
                    new SecretKeySpec(this.encoded, this.algo)
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateKeyContainer {

        private String algo;
        private String format;
        private byte[] encoded;

        public PrivateKeyContainer(PrivateKey key) {
            if (!PKCS_8.equals(key.getFormat())) {
                throw new IllegalArgumentException("Bad key format");
            }

            this.algo = key.getAlgorithm();
            this.format = key.getFormat();
            this.encoded = key.getEncoded();
        }
    }
}
