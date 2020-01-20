package de.adorsys.opba.core.protocol.service.protocol;

import de.adorsys.opba.core.protocol.repository.jpa.BankConfigurationRepository;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("protocolSelector")
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankConfigurationRepository config;

    @Transactional
    public String getProtocolForValidation(Xs2aContext ctx) {
        return config.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }

    @Transactional
    public String getProtocolForExecution(Xs2aContext ctx) {
        return config.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }
}
