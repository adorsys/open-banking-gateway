package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.service.AccountInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.TRANSACTIONS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1)
@RequiredArgsConstructor
public class AccountInformation {

    private final AccountInformationService ais;

    @GetMapping(TRANSACTIONS)
    public List<String> transactions() {
        return ais.transactionList();
    }
}
