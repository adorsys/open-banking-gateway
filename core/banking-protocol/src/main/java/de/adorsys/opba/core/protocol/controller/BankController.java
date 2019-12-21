package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.service.BankService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@Tag(
    name = "/v1/banks",
    description = "Bank search API",
    externalDocs = @ExternalDocumentation(description = "Search bank and select bank profile")
)
@CrossOrigin(origins = "*") //FIXME move CORS at gateway/load balancer level
public class BankController {

    private final BankService bankService;

    @GetMapping
    @Operation(summary = "Search banks by query string with limit of results")
    public List<Bank> getBanks(
            @Parameter(name = "q", description = "Query string", example = "commerz")
            @RequestParam("q") String query,
            @Parameter(name = "max_results", description = "Number of results in response", example = "10")
            @RequestParam("max_results") int maxResults) {
        return bankService.getBanks(query, maxResults);
    }

    @GetMapping("/fts")
    @Operation(summary = "Search banks using db full text search")
    public List<Bank> getBanksFTS(
            @Parameter(name = "q", description = "Query string", example = "commerz")
            @RequestParam("q") String query,
            @Parameter(name = "max_results", description = "Number of results in response", example = "10")
            @RequestParam("max_results") int maxResults) {
        return bankService.getBanksFTS(query, maxResults);
    }

    @GetMapping("/profile")
    @Operation(summary = "Getting bank profile by bank id")
    public Bank getBankProfile(
            @Parameter(name = "id", description = "Selected bank id", example = "142")
            @RequestParam Long id) {
        return bankService.getBankProfile(id);
    }
}

