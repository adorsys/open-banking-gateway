package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.opba.core.protocol.controller.constants.ApiPaths.BANKS;
import static de.adorsys.opba.core.protocol.controller.constants.ApiVersion.API_1;

@RestController
@RequestMapping(API_1 + BANKS)
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public List<Bank> getBanks(@RequestParam("q") String query,
                               @RequestParam("max_results") int maxResults) {
        return bankService.getBanks(query, maxResults);
    }

    @GetMapping("/profile")
    public Bank getBankProfile(@RequestParam Long id) {
        return bankService.getBankProfile(id);
    }
}

