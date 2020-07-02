package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.BpdAuthLevel;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("decisionSwitch")
public class DecisionSwitch {

    public boolean isDialogAnonymous(SandboxContext context) {
        return Strings.isNullOrEmpty(context.getRequestPin())
                || Operation.DIALOG_INIT_ANON == context.getRequestOperation();
    }

    public boolean isDialogPinTanOk(SandboxContext context) {
        return context.isPinOk() && context.isTanOk();
    }

    public boolean isWrongScaMethod(SandboxContext context) {
        if (Strings.isNullOrEmpty(context.getReferencedScaMethodId()) || !context.getUser().getScaMethodsAvailable().contains(context.getReferencedScaMethodId())) {
            log.warn("Wrong or missing TAN method ID: {} / allowed: {}", context.getReferencedScaMethodId(), context.getUser().getScaMethodsAvailable());
            return true;
        }

        return false;
    }

    public boolean isDialogPinOkAndNoTan(SandboxContext context) {
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
        return BpdAuthLevel.ANONYMOUS == context.getBank().getSecurity().getBankParametersData();
    }
}
