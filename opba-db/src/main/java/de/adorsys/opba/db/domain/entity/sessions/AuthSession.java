package de.adorsys.opba.db.domain.entity.sessions;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.fintech.FintechUser;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.protocol.api.services.EncryptionService;
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
import java.nio.charset.StandardCharsets;
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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private ServiceSession parent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BankProtocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    private Psu psu;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FintechUser fintechUser;

    @Column(nullable = false)
    private String redirectCode;
    private String aspspRedirectCode;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encConsentSpec;

    @Version
    private int version;

    public String getConsentSpec(EncryptionService encryption) {
        return new String(encryption.decrypt(encConsentSpec), StandardCharsets.UTF_8);
    }

    public void setConsentSpec(EncryptionService encryption, String context) {
        this.encConsentSpec = encryption.encrypt(context.getBytes(StandardCharsets.UTF_8));
    }
}
