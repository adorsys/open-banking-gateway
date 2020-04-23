package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "mode")
public class BankValidationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_validation_rule_id_generator")
    @SequenceGenerator(name = "bank_validation_rule_id_generator", sequenceName = "bank_validation_rule_sequence")
    private Long id;

    @ManyToOne(optional = false)
    private BankProtocol protocol;

    private String endpointClassCanonicalName;

    @Enumerated(EnumType.STRING)
    private FieldCode validationCode;

    private boolean forEmbedded;

    private boolean forRedirect;
}
