package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.BankSubProtocol;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProtocolSelector {

    private final ServiceSessionRepository sessions;
    private final BankProtocolRepository protocolRepository;

    @Transactional
    public <A> A selectAndPersistProtocolFor(
        ServiceContext<?> ctx,
        ProtocolAction protocolAction,
        Map<String, A> actionBeans) {
        Optional<BankProtocol> bankProtocol;

        if (null == ctx.getServiceBankProtocolId()) {
            bankProtocol = protocolRepository.findByBankProfileUuidAndAction(
                    ctx.getBankId(),
                    protocolAction
            );
        } else {
            Long id = isForAuthorization(protocolAction) ? ctx.getAuthorizationBankProtocolId() : ctx.getServiceBankProtocolId();
            bankProtocol = protocolRepository.findById(id);
        }

        return bankProtocol
                .map(protocol -> setProtocolOnSession(protocol, ctx.getServiceSessionId()))
                .map(protocol -> findActionBean(protocol, actionBeans, protocolAction))
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action bean for " + protocolAction.name() + " of: " + ctx.loggableBankId()
                        )
                );
    }

    private BankProtocol setProtocolOnSession(BankProtocol protocol, UUID serviceSessionId) {
        ServiceSession session = sessions.findById(serviceSessionId)
                .orElseThrow(() -> new IllegalStateException("Missing session " + serviceSessionId));

        if (null == session.getService()) {
            session.setService(protocol);
        }

        session.setProtocol(protocol);
        sessions.save(session);
        return protocol;
    }

    private boolean isForAuthorization(ProtocolAction action) {
        return action == ProtocolAction.AUTHORIZATION || ProtocolAction.AUTHORIZATION == action.getParent();
    }

    private <A> A findActionBean(BankProtocol forProtocol, Map<String, A> actionBeans, ProtocolAction action) {

        return actionBeans.getOrDefault(forProtocol.getProtocolBeanName(),
                                        findActionBeanFromSubProtocols(forProtocol.getSubProtocols(), actionBeans, action));
    }

    private <A> A findActionBeanFromSubProtocols(Collection<BankSubProtocol> subProtocols, Map<String, A> actionBeans, ProtocolAction action) {
        Optional<BankSubProtocol> subProtocol = subProtocols.stream()
                                                        .filter(it -> it.getAction() == action)
                                                        .findFirst();

        return subProtocol
                       .map(sub -> actionBeans.get(sub.getSubProtocolBeanName()))
                       .orElse(null);
    }
}
