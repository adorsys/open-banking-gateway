package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.service.BankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "/v1/banks", tags = "Bank search API", description = "Search bank and select bank profile")
@CrossOrigin(origins = "*")
public class BankController {

    private final BankService bankService;

    @GetMapping
    @ApiOperation("Search banks by query string with limit of results")
    public List<Bank> getBanks(
            @ApiParam(name = "q", value = "Query string", example = "commerz")
            @RequestParam("q") String query,
            @ApiParam(name = "max_results", value = "Number of results in response", example = "10")
            @RequestParam("max_results") int maxResults) {
        return bankService.getBanks(query, maxResults);
    }

    @GetMapping("/fts")
    @ApiOperation("Search banks using db full text search")
    public List<Bank> getBanksFTS(
            @ApiParam(name = "q", value = "Query string", example = "commerz")
            @RequestParam("q") String query,
            @ApiParam(name = "max_results", value = "Number of results in response", example = "10")
            @RequestParam("max_results") int maxResults) {
        return bankService.getBanksFTS(query, maxResults);
    }

    @GetMapping("/profile")
    @ApiOperation("Getting bank profile by bank id")
    public Bank getBankProfile(
            @ApiParam(name = "id", value = "Selected bank id", example = "142")
            @RequestParam Long id) {
        return bankService.getBankProfile(id);
    }
}

