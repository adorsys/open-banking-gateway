package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "mode")
public class ValidationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "validation_rule_id_generator")
    @SequenceGenerator(name = "validation_rule_id_generator", sequenceName = "validation_rule_sequence")
    private Long id;

    @ManyToOne(optional = false)
    private BankAction action;

    private String endpointClassCanonicalName;

    @Enumerated(EnumType.STRING)
    private FieldCode validationCode;

    private boolean forEmbedded;

    private boolean forRedirect;

    private boolean forDecoupled;
}
