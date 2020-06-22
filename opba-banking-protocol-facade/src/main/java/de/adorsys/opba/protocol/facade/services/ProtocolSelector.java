package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankSubAction;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankProtocolRepository protocolRepository;

    @Transactional
    public <I, A> InternalContext<I, A> selectProtocolFor(
        InternalContext<I, A> ctx,
        ProtocolAction protocolAction,
        Map<String, ? extends A> actionBeans) {
        Optional<BankAction> bankProtocol;

        if (null == ctx.getAuthSession()) {
            bankProtocol = protocolRepository.findByBankProfileUuidAndAction(
                    ctx.getServiceCtx().getBankId(),
                    protocolAction
            );
        } else {
            Long id = isForAuthorization(protocolAction) ? ctx.getServiceCtx().getAuthorizationBankProtocolId() : ctx.getServiceCtx().getServiceBankProtocolId();
            bankProtocol = protocolRepository.findById(id);
        }

        return bankProtocol
                .map(protocol -> {
                    A action = findActionBean(protocol, actionBeans, protocolAction);
                    return ctx.toBuilder()
                            .serviceCtx(ctx.getServiceCtx().toBuilder().serviceBankProtocolId(protocol.getId()).build())
                            .action(action)
                            .build();
                })
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action bean for " + protocolAction.name() + " of: " + ctx.getServiceCtx().loggableBankId()
                        )
                );
    }

    private boolean isForAuthorization(ProtocolAction action) {
        return action == ProtocolAction.AUTHORIZATION || ProtocolAction.AUTHORIZATION == action.getParent();
    }

    private <A> A findActionBean(BankAction forProtocol, Map<String, A> actionBeans, ProtocolAction action) {

        return actionBeans.getOrDefault(forProtocol.getProtocolBeanName(),
                                        findActionBeanFromSubProtocols(forProtocol.getSubProtocols(), actionBeans, action));
    }

    private <A> A findActionBeanFromSubProtocols(Collection<BankSubAction> subProtocols, Map<String, A> actionBeans, ProtocolAction action) {
        Optional<BankSubAction> subProtocol = subProtocols.stream()
                                                        .filter(it -> it.getProtocolAction() == action)
                                                        .findFirst();

        return subProtocol
                       .map(sub -> actionBeans.get(sub.getSubProtocolBeanName()))
                       .orElse(null);
    }
}
