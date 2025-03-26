package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.opba.db.domain.entity.IdAssignable;
import de.adorsys.opba.db.domain.generators.AssignedUuidGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FintechPrvKey implements IdAssignable<UUID> {

    @Id
    @GenericGenerator(
            name = AssignedUuidGenerator.ASSIGNED_ID_GENERATOR,
            strategy = AssignedUuidGenerator.ASSIGNED_ID_STRATEGY
    )
    @GeneratedValue(
            generator = AssignedUuidGenerator.ASSIGNED_ID_GENERATOR,
            strategy = GenerationType.AUTO
    )
    private UUID id;

    @OneToOne(mappedBy = "prvKey", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private FintechPubKey pubKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Fintech fintech;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encData;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
}
