package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.opba.db.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FintechPubKey {

    private static final String X_509 = "X.509";

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FintechPrvKey prvKey;

    private String algo;
    private String format;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;

    /**
     * Note that these are 'anonymous' for OPBA payments which uses Fintech key for encryption
     * and not all payments.
     */
    @OneToMany(mappedBy = "fintechPubKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Payment> payments;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

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
