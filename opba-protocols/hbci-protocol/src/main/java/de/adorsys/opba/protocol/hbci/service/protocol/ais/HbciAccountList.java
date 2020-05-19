package de.adorsys.opba.protocol.hbci.service.protocol.ais;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("hbciAccountList")
@RequiredArgsConstructor
public class HbciAccountList extends ValidatedExecution<HbciContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, HbciContext context) {
    }
}
