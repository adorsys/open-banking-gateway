package de.adorsys.opba.db.domain.entity.sessions;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthSession {

    @Id
    private UUID id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String context;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private ServiceSession parent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BankProtocol protocol;

    @Column(nullable = false)
    private String redirectCode;

    @Version
    private int version;
}
