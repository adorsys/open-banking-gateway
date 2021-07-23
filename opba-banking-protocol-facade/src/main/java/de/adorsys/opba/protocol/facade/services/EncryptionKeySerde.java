package de.adorsys.opba.protocol.facade.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.facade.config.encryption.SecretKeyWithIv;
import de.adorsys.opba.protocol.facade.dto.PubAndPrivKey;
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
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
@RequiredArgsConstructor
public class EncryptionKeySerde {

    private static final String PKCS_8 = "PKCS#8";
    private static final String X509 = "X.509";

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
    public void writeKey(PublicKey publicKey, PrivateKey privKey, OutputStream os) {
        // Mapper may choose to close the stream if using stream interface, we don't want this
        // as objects are small - this is ok.
        os.write(mapper.writeValueAsBytes(new PubAndPrivKeyContainer(publicKey, privKey)));
    }

    @SneakyThrows
    public PubAndPrivKey readKey(InputStream is) {
        PubAndPrivKeyContainer container = mapper.readValue(is, PubAndPrivKeyContainer.class);
        if (!PKCS_8.equals(container.getPrivFormat())) {
            throw new IllegalArgumentException("Bad key format");
        }
        if (!X509.equals(container.getPubFormat())) {
            throw new IllegalArgumentException("Bad key format");
        }

        KeyFactory factory = KeyFactory.getInstance(container.getAlgo());
        var privKey = factory.generatePrivate(new PKCS8EncodedKeySpec(container.getEncoded()));
        var pubKey = factory.generatePublic(new X509EncodedKeySpec(container.getPubEncoded()));
        return new PubAndPrivKey(pubKey, privKey);
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
    public static class PubAndPrivKeyContainer {

        private String algo;
        private String privFormat;
        private String pubFormat;
        private byte[] encoded;
        private byte[] pubEncoded;

        public PubAndPrivKeyContainer(PublicKey publicKey, PrivateKey privateKey) {
            if (!PKCS_8.equals(privateKey.getFormat())) {
                throw new IllegalArgumentException("Bad key format");
            }
            if (!X509.equals(publicKey.getFormat())) {
                throw new IllegalArgumentException("Bad key format");
            }

            this.algo = privateKey.getAlgorithm();
            this.privFormat = privateKey.getFormat();
            this.encoded = privateKey.getEncoded();
            this.pubFormat = publicKey.getFormat();
            this.pubEncoded = publicKey.getEncoded();
        }
    }
}
