package de.adorsys.opba.db.domain.entity;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
    @JoinColumn(name = "profile_uuid", referencedColumnName = "uuid", nullable = false)
    private BankProfile bankProfile;

    @Enumerated(EnumType.STRING)
    private ProtocolAction protocolAction;

    private String protocolBeanName;

    private boolean consentSupported;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<BankSubAction> subProtocols;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<AuthSession> authSessions;
}
