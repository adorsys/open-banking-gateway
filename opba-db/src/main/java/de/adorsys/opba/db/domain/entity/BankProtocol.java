package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class BankProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_protocol_id_generator")
    @SequenceGenerator(name = "bank_protocol_id_generator", sequenceName = "bank_protocol_id_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_uuid", referencedColumnName = "bank_uuid", nullable = false)
    private BankProfile bankProfile;

    @Enumerated(EnumType.STRING)
    private ProtocolAction action;

    private String protocolBeanName;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<BankSubProtocol> subProtocols;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<ServiceSession> currentForSessions;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<ServiceSession> servicesSessions;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AuthSession> authSessions;
}
