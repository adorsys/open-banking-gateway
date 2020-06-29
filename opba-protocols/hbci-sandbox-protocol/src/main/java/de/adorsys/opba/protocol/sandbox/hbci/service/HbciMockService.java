package de.adorsys.opba.protocol.sandbox.hbci.service;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing.ParsingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.kapott.hbci.protocol.Message;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;
import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.DIALOG_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HbciMockService {

    private static final String PROCESS_KEY = "hbci-dialog";

    private final RuntimeService runtimeService;

    public String handleRequest(String requestEncoded) {
        String toParse = ParsingUtil.cleanupCryptoHeaders(
                new String(Base64.getDecoder().decode(requestEncoded), StandardCharsets.ISO_8859_1)
        );
        Message message = ParsingUtil.parseMessage(toParse);
        String dialogId = message.getData().get(DIALOG_ID);

        if (null == dialogId || "".equals(dialogId) || "0".equals(dialogId)) {
            return triggerNewProcess(message);
        }

        return triggerExistingProcess(dialogId, message);
    }

    private String triggerNewProcess(Message request) {
        SandboxContext context = new SandboxContext();
        context.setRequest(buildRequest(request));
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, ImmutableMap.of(CONTEXT, context));

        return getResponse(instance.getId());
    }

    private String triggerExistingProcess(String dialogId, Message request) {
        SandboxContext context = (SandboxContext) runtimeService.getVariable(dialogId, CONTEXT);
        context.setRequest(buildRequest(request));
        runtimeService.setVariable(dialogId, CONTEXT, context);

        runtimeService.trigger(dialogId);

        return getResponse(dialogId);
    }

    private SandboxContext.Request buildRequest(Message request) {
        SandboxContext.Request mapped = new SandboxContext.Request();
        mapped.setData(request.getData());
        mapped.setOperation(Arrays.stream(Operation.values()).filter(it -> it.getValue().equals(request.getType()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown operation type: " + request.getType())));

        return mapped;
    }

    private String getResponse(String dialogId) {
        return ((SandboxContext) runtimeService.getVariable(dialogId, CONTEXT)).getResponse();
    }
}
