package de.adorsys.opba.protocol.sandbox.hbci.protocol.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Bank;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.User;
import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.MapRegexUtil;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.Operation;
import lombok.Data;

import java.util.Map;
import java.util.regex.Pattern;

@Data
public class HbciSandboxContext {

    private static final Pattern PIN = Pattern.compile("SigTail(_\\d+)*\\.UserSig(_\\d+)*\\.pin");
    private static final Pattern TAN = Pattern.compile("SigTail(_\\d+)*\\.UserSig(_\\d+)*\\.tan");
    private static final String BLZ = "Idn.KIK.blz";
    private static final String LOGIN = "Idn.customerid";

    private Request request;
    private Bank bank;
    private User user;
    private String accountNumberRequestedBeforeSca;
    private String referencedScaMethodId;
    private String paymentId;

    private String response;
    private boolean cryptNeeded;

    // commonly used:
    private String secCheckRef;
    private String dialogId;
    private String userId;
    private String sysId;
    private String orderReference;

    @JsonIgnore
    private HbciSandboxPayment payment;

    @JsonIgnore
    public Operation getRequestOperation() {
        return request.getOperation();
    }

    @JsonIgnore
    public Map<String, String> getRequestData() {
        return request.getData();
    }

    @JsonIgnore
    public boolean isPinOk() {
        if (null == getUser()) {
            return false;
        }

        return getUser().getPin().equals(getRequestPin());
    }

    @JsonIgnore
    public boolean isTanOk() {
        if (null == getUser()) {
            return false;
        }

        return getUser().getTan().equals(getRequestTan());
    }

    @JsonIgnore
    public boolean isTanEmpty() {
        if (null == getUser()) {
            return true;
        }

        return Strings.isNullOrEmpty(getRequestTan());
    }

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public String getRequestDataRegex(String regex) {
        return getRequest().getDataRegex(regex);
    }

    @JsonIgnore
    public String getRequestBankBlz() {
        return getRequestData().get(BLZ);
    }

    @JsonIgnore
    public String getRequestUserLogin() {
        return getRequestData().get(LOGIN);
    }

    @Data
    public static class Request {

        private Operation operation;
        private Map<String, String> data;

        @JsonIgnore
        public String getDataRegex(String regex) {
            return MapRegexUtil.getDataRegex(data, regex);
        }
    }
}
