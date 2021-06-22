package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.converter.ProtocolActionConverter;
import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Collection;

// TODO - do we need sequence?
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_action_id_generator")
    @SequenceGenerator(name = "bank_action_id_generator", sequenceName = "bank_action_id_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_uuid", referencedColumnName = "bank_uuid", nullable = false)
    private BankProfile bankProfile;

    @Convert(converter = ProtocolActionConverter.class)
    private ProtocolAction protocolAction;

    private String protocolBeanName;

    private boolean consentSupported;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<BankSubAction> subProtocols;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AuthSession> authSessions;
}
