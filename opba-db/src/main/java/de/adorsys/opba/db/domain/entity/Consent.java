package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consent_id_generator")
    @SequenceGenerator(name = "consent_id_generator", sequenceName = "consent_id_sequence")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceSession serviceSession;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bank aspsp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Psu psu;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encConsentId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encContext;

    private boolean confirmed;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public String getContext(EncryptionService encryption) {
        return new String(encryption.decrypt(encContext), StandardCharsets.UTF_8);
    }

    public void setContext(EncryptionService encryption, String context) {
        this.encContext = encryption.encrypt(context.getBytes(StandardCharsets.UTF_8));
    }

    public String getConsent(EncryptionService encryption) {
        return new String(encryption.decrypt(encConsentId), StandardCharsets.UTF_8);
    }

    public void setConsent(EncryptionService encryption, String consent) {
        this.encConsentId = encryption.encrypt(consent.getBytes(StandardCharsets.UTF_8));
    }
}

