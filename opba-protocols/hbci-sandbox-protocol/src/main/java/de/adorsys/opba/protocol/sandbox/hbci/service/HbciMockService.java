package de.adorsys.opba.protocol.sandbox.hbci.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing.ParsingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.kapott.hbci.protocol.Message;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.DIALOG_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HbciMockService {

    private static final String PROCESS_KEY = "hbci-dialog";

    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    public String handleRequest(String requestEncoded) {
        String decoded = new String(Base64.getDecoder().decode(requestEncoded), StandardCharsets.ISO_8859_1);
        boolean isCrypted = ParsingUtil.isCrypted(decoded);
        String toParse = ParsingUtil.cleanupCryptoHeaders(decoded);
        Message message = ParsingUtil.parseMessageWithoutSensitiveNonSensitiveValidation(toParse);
        String dialogId = message.getData().get(DIALOG_ID);

        if (null == dialogId || "".equals(dialogId) || "0".equals(dialogId)) {
            return triggerNewProcess(message, isCrypted);
        }

        return triggerExistingProcess(dialogId, message, isCrypted);
    }

    private String triggerNewProcess(Message request, boolean isCrypted) {
        SandboxContext context = new SandboxContext();
        context.setCryptNeeded(isCrypted);
        updateContextWithRequest(context, request);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, ImmutableMap.of(CONTEXT, context));

        return getResponse(instance.getId());
    }

    private String triggerExistingProcess(String dialogId, Message request, boolean isCrypted) {
        String execId = runtimeService.createActivityInstanceQuery()
                .processInstanceId(dialogId)
                .activityType("serviceTask")
                .unfinished()
                .singleResult()
                .getExecutionId();

        SandboxContext context = (SandboxContext) runtimeService.getVariable(execId, CONTEXT);
        context.setCryptNeeded(isCrypted);
        updateContextWithRequest(context, request);
        runtimeService.setVariable(execId, CONTEXT, context);

        runtimeService.trigger(execId);

        return getResponse(execId);
    }

    private void updateContextWithRequest(SandboxContext context, Message request) {
        SandboxContext.Request mapped = new SandboxContext.Request();
        mapped.setData(request.getData());
        context.setRequest(mapped);
        mapped.setOperation(Operation.find(context, request.getType()));
    }

    private String getResponse(String executionId) {
        try {
            return ((SandboxContext) runtimeService.getVariable(executionId, CONTEXT)).getResponse();
        } catch (FlowableObjectNotFoundException ex) {
            log.info("Can't find runtime instance of execution {} - looking in history tables", executionId);
            return readHistoricalContext(executionId).getResponse();
        }
    }

    private SandboxContext readHistoricalContext(String executionId) {
        HistoricActivityInstance finished = historyService.createHistoricActivityInstanceQuery()
                .executionId(executionId)
                .finished()
                .listPage(0, 1)
                .get(0);

        return (SandboxContext) historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(finished.getProcessInstanceId())
                .variableName(CONTEXT)
                .singleResult()
                .getValue();
    }
}
