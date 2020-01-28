package de.adorsys.opba.core.protocol.service.protocol;

import de.adorsys.opba.db.repository.jpa.BankConfigurationRepository;
import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("protocolSelector")
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankConfigurationRepository config;

    @Transactional
    public String getProtocolForValidation(BaseContext ctx) {
        return config.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }

    @Transactional
    public String getProtocolForExecution(BaseContext ctx) {
        return config.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }
}
