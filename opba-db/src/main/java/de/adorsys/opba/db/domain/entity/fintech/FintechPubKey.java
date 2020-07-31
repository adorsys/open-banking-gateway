package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.opba.db.domain.entity.BasePubKey;
import de.adorsys.opba.db.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
public class FintechPubKey extends BasePubKey {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private FintechPrvKey prvKey;

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
}
