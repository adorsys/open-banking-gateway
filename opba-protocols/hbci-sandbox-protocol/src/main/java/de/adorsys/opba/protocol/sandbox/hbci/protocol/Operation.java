package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized.AuthenticatedDialogInitSca;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum Operation {

    DIALOG_INIT("DialogInit", Operation::typeMatch),
    DIALOG_INIT_SCA_TAN_2_STEP("DialogInitScaTAN", Operation::isScaInit),
    DIALOG_INIT_ANON("DialogInitAnon", Operation::typeMatch),
    CUSTOM_MSG("CustomMsg", Operation::typeMatch),
    SYNCH("Synch", Operation::typeMatch),
    DIALOG_END("DialogEnd", Operation::typeMatch),
    ANY("*", Operation::typeMatch);

    private final String value;
    private final Function<MatchingContext, Boolean> is;

    public static Operation find(SandboxContext context, String xmlMessageType) {
        return Arrays.stream(Operation.values())
                .filter(it -> it.getIs().apply(new MatchingContext(it.getValue(), context, xmlMessageType)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown operation: " + xmlMessageType));
    }

    private static boolean typeMatch(MatchingContext context) {
        return context.getXmlMessageType().equals(context.getValue());
    }

    private static boolean isScaInit(MatchingContext context) {
        return AuthenticatedDialogInitSca.canHandle(context.getCtx())
                && (Operation.DIALOG_INIT.value.equals(context.getXmlMessageType()) || Operation.CUSTOM_MSG.value.equals(context.getXmlMessageType()));
    }

    @Data
    private static class MatchingContext {

        private final String value;
        private final SandboxContext ctx;
        private final String xmlMessageType;
    }
}
