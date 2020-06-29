package de.adorsys.opba.protocol.sandbox.hbci.protocol.common;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;

@Service("setDialogId")
public class SetDialogId implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        SandboxContext context = (SandboxContext) execution.getVariable(CONTEXT);
        context.setDialogId(execution.getRootProcessInstanceId());
        context.setSysId(String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));
        execution.setVariable(CONTEXT, context);
    }
}
