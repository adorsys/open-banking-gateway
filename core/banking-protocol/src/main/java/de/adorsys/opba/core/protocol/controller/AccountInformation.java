package de.adorsys.opba.core.protocol.controller;

import de.adorsys.xs2a.adapter.service.model.AccountDetails;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.ACCOUNTS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;
import static de.adorsys.opba.core.protocol.service.xs2a.accounts.AccountListConst.ACCOUNT_LIST;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final RuntimeService runtimeService;

    @GetMapping(ACCOUNTS)
    @Transactional
    public ResponseEntity<List<AccountDetails>> accounts() {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("listAccounts");
        return ResponseEntity.ok(((AccountListHolder) instance.getProcessVariables().get(ACCOUNT_LIST)).getAccounts());
    }
}
