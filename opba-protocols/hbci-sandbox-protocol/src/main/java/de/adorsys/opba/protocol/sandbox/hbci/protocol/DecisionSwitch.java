package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.AuthLevel;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import org.springframework.stereotype.Service;

@Service("decisionSwitch")
public class DecisionSwitch {

    public boolean isDialogAnonymous(SandboxContext context) {
        return Strings.isNullOrEmpty(context.getRequestPin())
                || Operation.DIALOG_INIT_ANON == context.getRequestOperation();
    }

    public boolean isDialogPinTanOk(SandboxContext context) {
        return context.isPinOk() && context.isTanOk();
    }

    public boolean isDialogOnlyPinOk(SandboxContext context) {
        return context.isPinOk() && context.isTanEmpty();
    }

    public boolean isDialogInit(SandboxContext context) {
        return Operation.DIALOG_INIT == context.getRequestOperation() && !isDialogInitScaTan2Step(context);
    }

    public boolean isDialogInitSca(SandboxContext context) {
        return Operation.DIALOG_INIT_SCA == context.getRequestOperation() && !isDialogInitScaTan2Step(context);
    }

    public boolean isDialogInitScaTan2Step(SandboxContext context) {
        return Operation.DIALOG_INIT_SCA_TAN_2_STEP == context.getRequestOperation();
    }

    public boolean isCustomMessage(SandboxContext context) {
        return Operation.CUSTOM_MSG == context.getRequestOperation();
    }

    public boolean isSynchronization(SandboxContext context) {
        return Operation.SYNCH == context.getRequestOperation();
    }

    public boolean isDialogEnd(SandboxContext context) {
        return Operation.DIALOG_END == context.getRequestOperation();
    }

    public boolean anonymousBpdSupported(SandboxContext context) {
        return AuthLevel.ANONYMOUS == context.getBank().getSecurity().getBankParametersData();
    }
}
