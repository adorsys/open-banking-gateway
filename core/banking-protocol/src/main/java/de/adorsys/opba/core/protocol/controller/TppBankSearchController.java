package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.service.BankService;
import de.adorsys.opba.tppbankingapi.search.model.BankDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse200;
import de.adorsys.opba.tppbankingapi.search.model.InlineResponse2001;
import de.adorsys.opba.tppbankingapi.search.resource.TppBankSearchApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private static final int DEFAULT_START = 0;
    private static final int DEFAULT_MAX = 10;

    private final BankService bankService;

    @Override
    public ResponseEntity<InlineResponse200> bankSearchGET(String authorization, UUID xRequestID, String keyword,
                                                           Integer start, Integer max) {
        if (start == null) {
            start = DEFAULT_START;
        }
        if (max == null) {
            max = DEFAULT_MAX;
        }
        List<Bank> banks = bankService.getBanks(keyword, start, max);
        if (banks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        InlineResponse200 inlineResponse200 = new InlineResponse200();
        banks.forEach(it -> inlineResponse200.addBankDescriptorItem(toBankDescriptor(it)));
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
        BankProfileDescriptor bankProfileDescriptor = new BankProfileDescriptor();
        bankProfileDescriptor.setBankName(bankProfile.get().getBank().getName());
        bankProfileDescriptor.setBic(bankProfile.get().getBank().getBic());
        if (StringUtils.isNotEmpty(bankProfile.get().getServices())) {
            bankProfileDescriptor.serviceList(Arrays.asList(bankProfile.get().getServices().split(",")));
        }
        inlineResponse2001.setBankProfileDescriptor(bankProfileDescriptor);
        return new ResponseEntity<>(inlineResponse2001, HttpStatus.OK);
    }

    private BankDescriptor toBankDescriptor(Bank bank) {
        BankDescriptor bankDescriptor = new BankDescriptor();
        bankDescriptor.setBankCode(bank.getBankCode());
        bankDescriptor.setBankName(bank.getName());
        bankDescriptor.setBic(bank.getBic());
        bankDescriptor.setUuid(bank.getUuid());
        return bankDescriptor;
    }
}

