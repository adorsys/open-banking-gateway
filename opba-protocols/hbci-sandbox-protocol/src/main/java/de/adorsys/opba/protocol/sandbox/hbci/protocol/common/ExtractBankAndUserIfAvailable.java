package de.adorsys.opba.protocol.sandbox.hbci.protocol.common;

import de.adorsys.opba.protocol.sandbox.hbci.config.HbciConfig;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.SandboxContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.sandbox.hbci.protocol.Const.CONTEXT;

@Slf4j
@Service("extractBankAndUser")
@RequiredArgsConstructor
public class ExtractBankAndUserIfAvailable implements JavaDelegate {

    private final HbciConfig config;

    @Override
    public void execute(DelegateExecution execution) {
        SandboxContext context = (SandboxContext) execution.getVariable(CONTEXT);

        updateBankIfNeeded(context, context.getRequestBankBlz());
        updateUserIfNeeded(context, context.getRequestUserLogin());
        
        execution.setVariable(CONTEXT, context);
    }

    private void updateBankIfNeeded(SandboxContext context, String bankBlz) {
        if (Strings.isBlank(bankBlz)) {
            return;
        }

        if (null != context.getBank()) {
            if (context.getBank().getBlz().equals(bankBlz)) {
                return;
            }

            throw new IllegalStateException(String.format("Bank BLZ redefinition: current: %s target %s", context.getBank().getBlz(), bankBlz));
        }

        log.info("Setting bank with BLZ {} for dialogId {}", bankBlz, context.getDialogId());
        context.setBank(config.getBanks().stream().filter(it -> it.getBlz().equals(bankBlz)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown bank BLZ: " + bankBlz))
        );
    }

    private void updateUserIfNeeded(SandboxContext context, String userLogin) {
        if (Strings.isBlank(userLogin)) {
            return;
        }

        if (null != context.getUser()) {
            if (context.getUser().getLogin().equals(userLogin)) {
                return;
            }

            throw new IllegalStateException(String.format("User redefinition: current: %s target %s", context.getUser().getLogin(), userLogin));
        }

        log.info("Setting user with login {} for dialogId {}", userLogin, context.getDialogId());
        // Not throwing for anonymous user
        context.setUser(config.getUsers().stream().filter(it -> it.getLogin().equals(userLogin)).findFirst()
                .orElseGet(() -> {
                    log.info("Login {} is anonymous for dialogId {}", userLogin, context.getDialogId());
                    return null;
                })
        );
    }
}
