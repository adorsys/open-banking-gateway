package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.service.BankService;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse200;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse2001;
import de.adorsys.opba.tppbankingapi.search.model.SearchInput;
import de.adorsys.opba.tppbankingapi.search.model.SearchMaxResult;
import de.adorsys.opba.tppbankingapi.search.model.SearchStartIndex;
import de.adorsys.opba.tppbankingapi.search.resource.TppBankSearchApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //FIXME move CORS at gateway/load balancer level
@SuppressWarnings("checkstyle:ParameterNumber") // FIXME
public class TppBankSearchController implements TppBankSearchApi {

    private final BankService bankService;

    @Override
    public ResponseEntity<InlineResponse200> bankSearchGET(String authorization,
                                                           UUID xRequestID,
                                                           SearchInput keyword,
                                                           SearchStartIndex start,
                                                           SearchMaxResult max) {
        int startInt;
        int maxInt;
        try {
            startInt = Integer.parseInt(start.toString());
            maxInt = Integer.parseInt(max.toString());
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Bank> banks = bankService.getBanks(keyword.toString(), startInt, maxInt);
        if (banks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        InlineResponse200 inlineResponse200 = new InlineResponse200();
        banks.forEach(it -> inlineResponse200.addBankDescriptorItem(Bank.TO_BANK_DESCRIPTOR.map(it)));
        inlineResponse200.setKeyword(keyword);
        inlineResponse200.setMax(max);
        inlineResponse200.setStart(start);
        return new ResponseEntity<>(inlineResponse200, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<InlineResponse2001> bankProfileGET(String authorization,
                                                             UUID xRequestID,
                                                             String bankId) {
        Optional<BankProfile> bankProfile = bankService.getBankProfile(bankId);
        if (!bankProfile.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        InlineResponse2001 inlineResponse2001 = new InlineResponse2001();
        BankProfileDescriptor bankProfileDescriptor = BankProfile.TO_BANK_PROFILE_DESCRIPTOR.map(bankProfile.get());
        bankProfileDescriptor.serviceList(Arrays.asList(bankProfile.get().getServices().split(",")));
        inlineResponse2001.setBankProfileDescriptor(bankProfileDescriptor);
        return new ResponseEntity<>(inlineResponse2001, HttpStatus.OK);
    }
}

