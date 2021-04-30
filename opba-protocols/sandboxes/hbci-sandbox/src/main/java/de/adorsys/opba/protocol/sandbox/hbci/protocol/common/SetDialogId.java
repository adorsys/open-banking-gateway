package de.adorsys.opba.protocol.sandbox.hbci.protocol.common;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.HbciSandboxLogResolver;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;

@Service("setDialogId")
public class SetDialogId implements JavaDelegate {

    private final HbciSandboxLogResolver logResolver = new HbciSandboxLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        HbciSandboxContext context = (HbciSandboxContext) execution.getVariable(CONTEXT);

        logResolver.log("execute: execution ({}) with context ({})", execution, context);

        context.setDialogId(execution.getRootProcessInstanceId());
        context.setSysId(String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));
        execution.setVariable(CONTEXT, context);

        logResolver.log("done execution ({}) with context ({})", execution, context);
    }
}
