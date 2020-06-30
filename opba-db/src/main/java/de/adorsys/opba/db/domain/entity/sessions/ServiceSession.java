package de.adorsys.opba.db.domain.entity.sessions;

import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.Consent;
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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
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
public class ServiceSession implements IdAssignable<UUID>  {

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

    @OneToOne(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AuthSession authSession;

    /**
     * Identifies which bank is served with this session.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BankProfile bankProfile;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "serviceSession")
    private Collection<Consent> consents;

    @Version
    private int version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
}
