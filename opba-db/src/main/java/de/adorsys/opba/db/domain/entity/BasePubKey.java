package de.adorsys.opba.db.domain.entity;

import lombok.Data;
import lombok.SneakyThrows;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Data
@MappedSuperclass
public abstract class BasePubKey {

    private static final String X_509 = "X.509";

    private String algo;
    private String format;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;

    @SneakyThrows
    public PublicKey getKey() {
        if (!X_509.equals(format)) {
            throw new IllegalArgumentException("Bad key format");
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory kf = KeyFactory.getInstance(algo);
        return kf.generatePublic(spec);
    }

    @SneakyThrows
    public void setKey(PublicKey key) {
        if (!X_509.equals(key.getFormat())) {
            throw new IllegalArgumentException("Bad key format");
        }

        this.algo = key.getAlgorithm();
        this.format = key.getFormat();
        this.data = key.getEncoded();
    }
}
