package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.converter.ProtocolActionConverter;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
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

    @Convert(converter = ProtocolActionConverter.class)
    private ProtocolAction protocolAction;

    private String subProtocolBeanName;
}
