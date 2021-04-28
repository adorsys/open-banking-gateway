package de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.domain;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import lombok.ToString;


@ToString(callSuper = true)
public class HbciSandboxContextLog extends HbciSandboxContext {

    public String getNotSensitiveData() {
        return "HbciSandboxContextLog("
                + "request=" + this.getRequest()
                + ", dialogId=" + this.getDialogId()
                + ", userId=" + this.getUserId()
                + ", sysId=" + this.getSysId()
                + ")";
    }
}
