package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.BankProtocol;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankProtocolRepository protocolRepository;

    @Transactional(readOnly = true)
    public <A> A protocolFor(ServiceContext<?> ctx, ProtocolAction protocolAction, Map<String, A> actionBeans) {
        Optional<BankProtocol> bankProtocol;

        if (null == ctx.getBankProtocolId()) {
            bankProtocol = protocolRepository.findByBankProfileUuidAndAction(
                    ctx.getFacadeServiceable().getBankID(),
                    protocolAction
            );
        } else {
            bankProtocol = protocolRepository.findById(ctx.getBankProtocolId());
        }

        return bankProtocol.map(protocol -> actionBeans.get(protocol.getProtocolBeanName()))
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action bean for " + protocolAction.name() + " of: " + bankIdString(ctx)
                        )
                );
    }

    private String bankIdString(ServiceContext<?> ctx) {
        return String.format(
                "[protocol id: %s / bank uuid: %s]",
                ctx.getBankProtocolId(),
                ctx.getFacadeServiceable().getBankID()
        );
    }
}
