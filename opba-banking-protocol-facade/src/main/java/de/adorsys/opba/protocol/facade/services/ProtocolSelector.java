package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.db.domain.entity.BankSubAction;
import de.adorsys.opba.db.repository.jpa.BankActionRepository;
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

    private final BankActionRepository bankActionRepository;

    @Transactional
    public <REQUEST, ACTION> Optional<InternalContext<REQUEST, ACTION>> selectProtocolFor(
            InternalContext<REQUEST, ACTION> ctx,
            ProtocolAction protocolAction,
            Map<String, ? extends ACTION> actionBeans) {
        Optional<BankAction> bankAction;

        if (null == ctx.getAuthSession()) {
            bankAction = bankActionRepository.findByBankProfileUuidAndAction(
                    ctx.getServiceCtx().getBankProfileId(),
                    protocolAction
            );
        } else {
            Long id = isForAuthorization(protocolAction) ? ctx.getServiceCtx().getAuthorizationBankProtocolId() : ctx.getServiceCtx().getServiceBankProtocolId();
            bankAction = bankActionRepository.findById(id);
        }

        return bankAction
                .map(action -> {
                    ACTION actionBean = findActionBean(action, actionBeans, protocolAction);
                    return ctx.toBuilder()
                            .serviceCtx(ctx.getServiceCtx().toBuilder().serviceBankProtocolId(action.getId()).build())
                            .action(actionBean)
                            .build();
                });
    }

    @Transactional
    public <REQUEST, ACTION> InternalContext<REQUEST, ACTION> requireProtocolFor(
        InternalContext<REQUEST, ACTION> ctx,
        ProtocolAction protocolAction,
        Map<String, ? extends ACTION> actionBeans) {
        return selectProtocolFor(ctx, protocolAction, actionBeans)
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
