package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.domain.generators.AssignedUuidGenerator;
import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Consent {

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

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceSession serviceSession;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bank aspsp;

    /**
     * If the consent encryption key can be identified by the PSU + ASPSP encryption key.
     */
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encCache;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] encSupplementaryKey;

    /**
     * If the consent encryption key can be identified by Key ID stored in FinTech keystore (anonymous consent).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private FintechPubKey fintechPubKey;

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

    public String getCache(EncryptionService encryption) {
        if (null == encCache) {
            return null;
        }

        return new String(encryption.decrypt(encCache), StandardCharsets.UTF_8);
    }

    public void setCache(EncryptionService encryption, String cache) {
        this.encCache = encryption.encrypt(cache.getBytes(StandardCharsets.UTF_8));
    }

    public String getEncSupplementaryKey(EncryptionService encryption) {
        if (null == encSupplementaryKey) {
            return null;
        }

        return new String(encryption.decrypt(encSupplementaryKey), StandardCharsets.UTF_8);
    }

    public void setEncSupplementaryKey(EncryptionService encryption, String encSupplementaryKey) {
        this.encSupplementaryKey = encryption.encrypt(encSupplementaryKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getConsentId(EncryptionService encryption) {
        byte[] decryptedConsent = encryption.decrypt(encConsentId);
        if (null == decryptedConsent) {
            return null;
        }

        return new String(decryptedConsent, StandardCharsets.UTF_8);
    }

    public void setConsentId(EncryptionService encryption, String consent) {
        byte[] consentToEncrypt = null == consent ? null : consent.getBytes(StandardCharsets.UTF_8);
        this.encConsentId = encryption.encrypt(consentToEncrypt);
    }
}

