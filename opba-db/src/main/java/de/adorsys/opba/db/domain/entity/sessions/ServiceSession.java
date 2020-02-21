package de.adorsys.opba.db.domain.entity.sessions;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.IdAssignable;
import de.adorsys.opba.db.domain.generators.AssignedUuidGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String context;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String fintechOkUri;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String fintechNokUri;

    @OneToOne(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AuthSession authSession;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BankProtocol protocol;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "serviceSession")
    private Collection<Consent> consents;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] secretKey;

    private String algo;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] salt;

    private int iterCount;

    @Version
    private int version;
}
