package de.adorsys.opba.protocol.sandbox.hbci.protocol.context;

import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import lombok.Data;

import java.util.Map;
import java.util.regex.Pattern;

@Data
public class SandboxContext {

    private static final Pattern PIN = Pattern.compile("SigTail(?:_\\d+)\\.UserSig(?:_\\d+)\\.pin");
    private static final Pattern TAN = Pattern.compile("SigTail(?:_\\d+)\\.UserSig(?:_\\d+)\\.tan");
    private static final String BLZ = "Idn.KIK.blz";
    private static final String LOGIN = "Idn.customerid";

    private Request request;
    private Bank bank;
    private User user;

    private String response;

    // commonly used:
    private String secCheckRef;
    private String dialogId;
    private String userId;
    private String sysId;

    public Operation getRequestOperation() {
        return request.getOperation();
    }

    public Map<String, String> getRequestData() {
        return request.getData();
    }

    public boolean isPinOk() {
        return getUser().getPin().equals(getRequestPin());
    }

    public boolean isTanOk() {
        return getUser().getTan().equals(getRequestTan());
    }

    public String getRequestPin() {
        if (null == request || null == getRequestData()) {
            return null;
        }

        return getRequestData().entrySet().stream()
                .filter(it -> PIN.matcher(it.getKey()).find())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public String getRequestTan() {
        if (null == request || null == getRequestData()) {
            return null;
        }

        return getRequestData().entrySet().stream()
                .filter(it -> TAN.matcher(it.getKey()).find())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public String getRequestBankBlz() {
        return getRequestData().get(BLZ);
    }

    public String getRequestUserLogin() {
        return getRequestData().get(LOGIN);
    }

    @Data
    public static class Request {

        private Operation operation;
        private Map<String, String> data;
    }
}
