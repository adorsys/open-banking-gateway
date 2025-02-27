package de.adorsys.opba.db.domain.entity.fintech;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.IdAssignable;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.generators.AssignedUuidGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FintechPsuAspspKey implements IdAssignable<UUID> {

    @Id
    @GenericGenerator(
            name = AssignedUuidGenerator.ASSIGNED_ID_GENERATOR,
            strategy = AssignedUuidGenerator.ASSIGNED_ID_STRATEGY
    )
    @GeneratedValue(
            generator = AssignedUuidGenerator.ASSIGNED_ID_GENERATOR,
            strategy = GenerationType.AUTO
    )
    protected UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected Fintech fintech;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected Psu psu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    protected Bank aspsp;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    protected byte[] encData;

    @CreatedDate
    protected Instant createdAt;

    @LastModifiedDate
    protected Instant modifiedAt;
}
