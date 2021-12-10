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

/**
 * Allows serializing encryption keys to string/stream and back.
 */
@Service
@RequiredArgsConstructor
public class EncryptionKeySerde {

    private static final String PKCS_8 = "PKCS#8";
    private static final String X509 = "X.509";

    private final ObjectMapper mapper;

    /**
     * Convert symmetric key with initialization vector to string.
     * @param secretKeyWithIv Symmetric Key + IV
     * @return Serialized key
     */
    @SneakyThrows
    public String asString(SecretKeyWithIv secretKeyWithIv) {
        return mapper.writeValueAsString(new SecretKeyWithIvContainer(secretKeyWithIv));
    }

    /**
     * Convert string to symmetric key with initialization vector.
     * @param fromString String to buld key from
     * @return Deserialized key
     */
    @SneakyThrows
    public SecretKeyWithIv fromString(String fromString) {
        SecretKeyWithIvContainer container = mapper.readValue(fromString, SecretKeyWithIvContainer.class);
        return new SecretKeyWithIv(
                container.getIv(),
                new SecretKeySpec(container.getEncoded(), container.getAlgo())
        );
    }

    /**
     * Write symmetric key with initialization vector to output stream.
     * @param value Key to write
     * @param os Output stream to write to
     */
    @SneakyThrows
    public void write(SecretKeyWithIv value, OutputStream os) {
        // Mapper may choose to close the stream if using stream interface, we don't want this
        // as objects are small - this is ok.
        os.write(mapper.writeValueAsBytes(new SecretKeyWithIvContainer(value)));
    }

    /**
     * Read symmetric key with initialization vector from input stream.
     * @param is Stream with key
     * @return Read key
     */
    @SneakyThrows
    public SecretKeyWithIv read(InputStream is) {
        SecretKeyWithIvContainer container = mapper.readValue(is, SecretKeyWithIvContainer.class);
        return new SecretKeyWithIv(
                container.getIv(),
                new SecretKeySpec(container.getEncoded(), container.getAlgo())
        );
    }

    /**
     * Write public-private key pair into OutputStream
     * @param publicKey Public key of pair
     * @param privKey Private key of pair
     * @param os Output stream to write to
     */
    @SneakyThrows
    public void writeKey(PublicKey publicKey, PrivateKey privKey, OutputStream os) {
        // Mapper may choose to close the stream if using stream interface, we don't want this
        // as objects are small - this is ok.
        os.write(mapper.writeValueAsBytes(new PubAndPrivKeyContainer(publicKey, privKey)));
    }

    /**
     * Read public-private key pair from InputStream
     * @param is InputStream to read key from
     * @return Read key pair
     */
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

    /**
     * Container for the symmetric key and initialization vector.
     */
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

    /**
     * Container for public-private key pair
     */
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
