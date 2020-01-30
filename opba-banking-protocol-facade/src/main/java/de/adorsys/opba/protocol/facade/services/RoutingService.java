package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoutingService {

    private final BankProtocolRepository protocolRepository;

    @Transactional
    public <A> A protocolFor(String bankUuid, ProtocolAction protocolAction, Map<String, A> actionBeans) {
        return protocolRepository.findByBankProfileUuidAndAction(bankUuid, protocolAction)
                .map(protocol -> actionBeans.get(protocol.getProtocolBeanName()))
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No action bean for " + protocolAction.name() + " of bank uuid: " + bankUuid
                        )
                );
    }
}
