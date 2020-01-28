package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.Approach;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.HashMap;
import java.util.Map;

// TODO - do we need sequence?
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_configuration_id_generator")
    @SequenceGenerator(name = "bank_configuration_id_generator", sequenceName = "bank_configuration_id_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Approach preferredApproach;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "configuration")
    @MapKey(name = "action")
    private Map<ProtocolAction, BankProtocol> actions = new HashMap<>();
}
