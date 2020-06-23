package de.adorsys.opba.fintech.impl.database.entities;

import de.adorsys.opba.fintech.impl.tppclients.ConsentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
    private Boolean consentConfirmed;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

}
