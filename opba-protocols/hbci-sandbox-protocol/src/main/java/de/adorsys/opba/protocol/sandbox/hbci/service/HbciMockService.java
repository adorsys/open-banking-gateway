package de.adorsys.opba.protocol.sandbox.hbci.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.DIALOG_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HbciMockService {

    private static final Pattern SCA_METHOD_ID = Pattern.compile("HNSHK:\\d+?:\\d+?\\+PIN:\\d+?\\+(\\d+?)\\+");
    private static final String PROCESS_KEY = "hbci-dialog";

    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    public String handleRequest(String requestEncoded) {
        String decoded = new String(Base64.getDecoder().decode(requestEncoded), StandardCharsets.ISO_8859_1);
        boolean isCrypted = ParsingUtil.isCrypted(decoded);
        String toParse = ParsingUtil.cleanupCryptoHeaders(decoded);
        List<Message> messages = ParsingUtil.parseMessageWithoutSensitiveNonSensitiveValidation(toParse);
        Operation.Match match = Operation.find(messages);
        Message message = match.getMessage();
        String dialogId = message.getData().get(DIALOG_ID);
        String scaMethodId = extractScaMethodId(decoded);

        if (null == dialogId || "".equals(dialogId) || "0".equals(dialogId)) {
            return triggerNewProcess(match.getOperation(), message, scaMethodId, isCrypted);
        }

        return triggerExistingProcess(dialogId, match.getOperation(), message, scaMethodId, isCrypted);
    }

    private String extractScaMethodId(String decoded) {
        Matcher matcher = SCA_METHOD_ID.matcher(decoded);
        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1);
    }

    private String triggerNewProcess(Operation operation, Message request, String scaMethodId, boolean isCrypted) {
        HbciSandboxContext context = new HbciSandboxContext();
        context.setCryptNeeded(isCrypted);
        setRequest(operation, request, context);
        context.setReferencedScaMethodId(scaMethodId);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, ImmutableMap.of(CONTEXT, context));

        return getResponse(instance.getId());
    }

    private void setRequest(Operation operation, Message request, HbciSandboxContext context) {
        HbciSandboxContext.Request update = new HbciSandboxContext.Request();
        update.setData(request.getData());
        update.setOperation(operation);
        context.setRequest(update);
    }

    private String triggerExistingProcess(String dialogId, Operation operation, Message request, String scaMethodId, boolean isCrypted) {
        String execId = runtimeService.createActivityInstanceQuery()
                .processInstanceId(dialogId)
                .activityType("serviceTask")
                .unfinished()
                .singleResult()
                .getExecutionId();

        HbciSandboxContext context = (HbciSandboxContext) runtimeService.getVariable(execId, CONTEXT);
        context.setCryptNeeded(isCrypted);
        setRequest(operation, request, context);
        context.setReferencedScaMethodId(scaMethodId);
        runtimeService.setVariable(execId, CONTEXT, context);

        runtimeService.trigger(execId);

        return getResponse(execId);
    }

    private String getResponse(String executionId) {
        try {
            return ((HbciSandboxContext) runtimeService.getVariable(executionId, CONTEXT)).getResponse();
        } catch (FlowableObjectNotFoundException ex) {
            log.info("Can't find runtime instance of execution {} - looking in history tables", executionId);
            return readHistoricalContext(executionId).getResponse();
        }
    }

    private HbciSandboxContext readHistoricalContext(String executionId) {
        HistoricActivityInstance finished = historyService.createHistoricActivityInstanceQuery()
                .executionId(executionId)
                .finished()
                .listPage(0, 1)
                .get(0);

        return (HbciSandboxContext) historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(finished.getProcessInstanceId())
                .variableName(CONTEXT)
                .singleResult()
                .getValue();
    }
}
