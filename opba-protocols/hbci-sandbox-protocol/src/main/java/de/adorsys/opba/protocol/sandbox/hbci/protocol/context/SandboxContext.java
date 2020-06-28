package de.adorsys.opba.protocol.sandbox.hbci.protocol.context;

import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Account;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import lombok.Data;

import java.util.Map;

@Data
public class SandboxContext {

    private Operation requestOperation;
    private Map<String, String> requestData;
    private String requestPin;
    private String requestTan;

    private Bank bank;
    private User user;
    private Account account;

    private boolean missingOrWrongTan;
    private boolean missingOrWrongPin;

    private String response;

    // commonly used:
    private String secCheckRef;
    private String dialogId;
    private String userId;
}
