package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankSubAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_sub_action_id_generator")
    @SequenceGenerator(name = "bank_sub_action_id_generator", sequenceName = "bank_sub_action_id_sequence")
    private Long id;

    @ManyToOne(optional = false)
    private BankAction action;

    @Enumerated(EnumType.STRING)
    private ProtocolAction protocolAction;

    private String subProtocolBeanName;
}
