package de.adorsys.opba.protocol.sandbox.hbci.protocol.context;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import lombok.Data;

import java.util.Map;

@Data
public class SandboxContext {

    private Operation requestOperation;
    private Map<String, String> requestData;

    private boolean missingOrWrongTan;
    private boolean missingOrWrongPin;

    private String response;
}
