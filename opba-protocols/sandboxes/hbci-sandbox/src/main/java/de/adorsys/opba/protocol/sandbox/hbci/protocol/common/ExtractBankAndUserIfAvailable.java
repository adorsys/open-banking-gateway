package de.adorsys.opba.protocol.sandbox.hbci.protocol.common;

import de.adorsys.opba.protocol.sandbox.hbci.config.HbciConfig;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.context.HbciSandboxContext;
import de.adorsys.opba.protocol.sandbox.hbci.util.logresolver.HbciSandboxLogResolver;
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
    private final HbciSandboxLogResolver logResolver = new HbciSandboxLogResolver(getClass());

    @Override
    public void execute(DelegateExecution execution) {
        HbciSandboxContext context = (HbciSandboxContext) execution.getVariable(CONTEXT);

        logResolver.log("execute: execution ({}) with context ({})", execution, context);

        updateBankIfNeeded(context, context.getRequestBankBlz());
        updateUserIfNeeded(context, context.getRequestUserLogin());

        execution.setVariable(CONTEXT, context);

        logResolver.log("done execution ({}) with context ({})", execution, context);
    }

    private void updateBankIfNeeded(HbciSandboxContext context, String bankBlz) {
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

    private void updateUserIfNeeded(HbciSandboxContext context, String userLogin) {
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
