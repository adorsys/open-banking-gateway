package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consent_id_generator")
    @SequenceGenerator(name = "consent_id_generator", sequenceName = "consent_id_sequence")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServiceSession serviceSession;

    private String consentCode;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String context;
}

