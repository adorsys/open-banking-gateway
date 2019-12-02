package de.adorsys.opba.core.protocol.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @SequenceGenerator(name = "consent_id_generator", sequenceName = "consent_id_sequence", allocationSize = 1)
    private Long id;

    private String consentCode;
}
