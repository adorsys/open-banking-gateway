package de.adorsys.opba.protocol.xs2a.service.protocol;

import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.BaseContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("protocolSelector")
@RequiredArgsConstructor
public class ProtocolSelector {

    private final BankProfileJpaRepository bankProfileJpaRepository;

    @Transactional
    public String getProtocolForValidation(BaseContext ctx) {
        return bankProfileJpaRepository.findByBankUuid(ctx.getAspspId()).get()
                .getActions().get(ctx.getAction()).getProcessName();
    }

    @Transactional
    public String getProtocolForExecution(BaseContext ctx) {
        return bankProfileJpaRepository.findByBankUuid(ctx.getAspspId()).get()
                .getActions().get(ctx.getAction()).getProcessName();
    }
}
