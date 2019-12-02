package de.adorsys.opba.core.protocol.controller;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final RuntimeService runtimeService;

    @GetMapping(TRANSACTIONS)
    @Transactional
    public ResponseEntity<List<String>> transactions() {
        runtimeService.startProcessInstanceByKey("createConsent");

        return ResponseEntity.ok(Collections.emptyList());
    }
}
