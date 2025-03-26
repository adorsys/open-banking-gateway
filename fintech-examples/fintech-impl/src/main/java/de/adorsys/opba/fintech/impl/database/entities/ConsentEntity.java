package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
public class ConsentEntity {
    public ConsentEntity(ConsentType consentType, UserEntity userEntity, String bankId, String accountId, String tppAuthId, UUID tppServiceSessionId) {
        this.consentType = consentType;
        this.userEntity = userEntity;
        this.consentConfirmed = false;
        this.tppServiceSessionId = tppServiceSessionId;
        this.tppAuthId = tppAuthId;
        this.bankId = bankId;
        this.accountId = accountId;
        this.creationTime = OffsetDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consent_generator")
    @SequenceGenerator(name = "consent_generator", sequenceName = "consent_id_seq")
    private Long id;

    private String bankId;
    private String accountId;
    private String tppAuthId;
    private UUID tppServiceSessionId;
    private ConsentType consentType;
    @Column(nullable = false)
    private OffsetDateTime creationTime;

    @Column(nullable = false)
    private Boolean consentConfirmed;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

}
