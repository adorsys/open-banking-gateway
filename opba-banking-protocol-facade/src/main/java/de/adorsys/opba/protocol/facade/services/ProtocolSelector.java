package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProtocolSelector {

    private final ServiceSessionRepository sessions;
    private final BankProtocolRepository protocolRepository;

    @Transactional(readOnly = true)
    public <A> A selectAndPersistProtocolFor(
            ServiceContext<?> ctx,
            ProtocolAction protocolAction,
            Map<String, A> actionBeans) {
        Optional<BankProtocol> bankProtocol;

        if (null == ctx.getBankProtocolId()) {
            bankProtocol = protocolRepository.findByBankProfileUuidAndAction(
                    ctx.getBankId(),
                    protocolAction
            );
        } else {
            bankProtocol = protocolRepository.findById(ctx.getBankProtocolId());
        }

        return bankProtocol
                .map(protocol -> setProtocolOnSession(protocol, ctx.getServiceSessionId()))
                .map(protocol -> actionBeans.get(protocol.getProtocolBeanName()))
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action bean for " + protocolAction.name() + " of: " + bankIdString(ctx)
                        )
                );
    }

    private BankProtocol setProtocolOnSession(BankProtocol protocol, UUID serviceSessionId) {
        ServiceSession session = sessions.findById(serviceSessionId)
                .orElseThrow(() -> new IllegalStateException("Missing session " + serviceSessionId));

        session.setProtocol(protocol);
        sessions.save(session);
        return protocol;
    }

    private String bankIdString(ServiceContext<?> ctx) {
        return String.format(
                "[protocol id: %s / bank uuid: %s]",
                ctx.getBankProtocolId(),
                ctx.getBankId()
        );
    }
}
