package de.adorsys.opba.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankValidationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_validation_rule_id_generator")
    @SequenceGenerator(name = "bank_validation_rule_id_generator", sequenceName = "bank_validation_rule_sequence")
    private Long id;

    @ManyToOne(optional = false)
    private BankProtocol protocol;

    private String endpointClass;

    private String validationCode;

    private boolean forEmbedded;

    private boolean forRedirect;
}
