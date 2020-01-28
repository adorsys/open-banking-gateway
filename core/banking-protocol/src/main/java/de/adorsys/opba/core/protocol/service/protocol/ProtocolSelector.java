package de.adorsys.opba.core.protocol.service.protocol;

import de.adorsys.opba.core.protocol.service.xs2a.context.BaseContext;
import de.adorsys.opba.db.repository.jpa.BankProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("protocolSelector")
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankProfileRepository bankProfileRepository;

    @Transactional
    public String getProtocolForValidation(BaseContext ctx) {
        return bankProfileRepository.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }

    @Transactional
    public String getProtocolForExecution(BaseContext ctx) {
        return bankProfileRepository.getOne(ctx.getBankConfigId()).getActions().get(ctx.getAction()).getProcessName();
    }
}
