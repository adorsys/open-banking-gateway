package de.adorsys.opba.core.protocol.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;
import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;

@RestController
@RequestMapping(API_1)
public class AccountInformation {

    @GetMapping(TRANSACTIONS)
    public List<String> transactions() {
        return Collections.emptyList();
    }
}
