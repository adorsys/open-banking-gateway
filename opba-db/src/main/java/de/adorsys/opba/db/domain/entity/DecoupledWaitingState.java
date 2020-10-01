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
import javax.persistence.SequenceGenerator;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecoupledWaitingState {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decopled_waiting_state_generator")
    @SequenceGenerator(name = "decopled_waiting_state_generator", sequenceName = "decopled_waiting_state_sequence")
    private Long id;

    private UUID consentPaymentid;
    private UUID executionId;
    private boolean decoupledAuthorisation;
}
