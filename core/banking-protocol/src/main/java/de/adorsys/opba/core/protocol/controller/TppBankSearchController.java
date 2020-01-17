package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.service.BankService;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileResponse;
import de.adorsys.opba.tppbankingapi.search.model.BankSearchResponse;
import de.adorsys.opba.tppbankingapi.search.resource.TppBankSearchApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //FIXME move CORS at gateway/load balancer level
@SuppressWarnings("checkstyle:ParameterNumber")
public class TppBankSearchController implements TppBankSearchApi {
    @Value("${bank-search.start:0}") int defaultStart;
    @Value("${bank-search.max:10}") int defaultMax;

    private final BankService bankService;

    @Override
    public ResponseEntity<BankSearchResponse> bankSearchGET(String authorization, UUID xRequestID, String keyword,
                                                            Integer start, Integer max) {
        if (start == null) {
            start = defaultStart;
        }
        if (max == null) {
            max = defaultMax;
        }
        List<Bank> banks = bankService.getBanks(keyword, start, max);

        BankSearchResponse response = new BankSearchResponse();
        banks.forEach(it -> response.addBankDescriptorItem(Bank.TO_BANK_DESCRIPTOR.map(it)));
        response.setKeyword(keyword);
        response.setMax(max);
        response.setStart(start);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BankProfileResponse> bankProfileGET(String authorization,
                                                              UUID xRequestID,
                                                              String bankId) {
        Optional<BankProfile> bankProfile = bankService.getBankProfile(bankId);
        if (!bankProfile.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        BankProfileResponse response = new BankProfileResponse();
        response.setBankProfileDescriptor(BankProfile.TO_BANK_PROFILE_DESCRIPTOR.map(bankProfile.get()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

