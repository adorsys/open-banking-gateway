package de.adorsys.opba.db.domain.entity.sessions;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.fintech.FintechConsentSpec;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.nio.charset.StandardCharsets;
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
public class AuthSession {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private ServiceSession parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private BankProtocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    private Psu psu;

    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private FintechUser fintechUser;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "authSession")
    private Collection<FintechConsentSpec> consentSpecs;

    @Column(nullable = false)
    private String redirectCode;
    private String aspspRedirectCode;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encConsentSpec;

    private String context;

    @Version
    private int version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public String getConsentSpec(EncryptionService encryption) {
        return new String(encryption.decrypt(encConsentSpec), StandardCharsets.UTF_8);
    }

    public void setConsentSpec(EncryptionService encryption, String context) {
        this.encConsentSpec = encryption.encrypt(context.getBytes(StandardCharsets.UTF_8));
    }
}
