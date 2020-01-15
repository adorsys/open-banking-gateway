package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.domain.entity.Bank;
import de.adorsys.opba.core.protocol.domain.entity.BankProfile;
import de.adorsys.opba.core.protocol.service.BankService;
import de.adorsys.opba.tppbankingapi.search.model.BankDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileResponse;
import de.adorsys.opba.tppbankingapi.search.model.BankSearchResponse;
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
@SuppressWarnings("checkstyle:ParameterNumber")
public class TppBankSearchController implements TppBankSearchApi {
    private static final int DEFAULT_START = 0;
    private static final int DEFAULT_MAX = 10;

    private final BankService bankService;

    @Override
    public ResponseEntity<BankSearchResponse> bankSearchGET(String authorization, UUID xRequestID, String keyword,
                                                            Integer start, Integer max) {
        if (start == null) {
            start = DEFAULT_START;
        }
        if (max == null) {
            max = DEFAULT_MAX;
        }
        List<Bank> banks = bankService.getBanks(keyword, start, max);

        BankSearchResponse response = new BankSearchResponse();
        banks.forEach(it -> response.addBankDescriptorItem(toBankDescriptor(it)));
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
        BankProfileDescriptor bankProfileDescriptor = new BankProfileDescriptor();
        bankProfileDescriptor.setBankName(bankProfile.get().getBank().getName());
        bankProfileDescriptor.setBic(bankProfile.get().getBank().getBic());
        if (StringUtils.isNotEmpty(bankProfile.get().getServices())) {
            bankProfileDescriptor.serviceList(Arrays.asList(bankProfile.get().getServices().split(",")));
        }
        BankProfileResponse response = new BankProfileResponse();
        response.setBankProfileDescriptor(bankProfileDescriptor);
        return new ResponseEntity<>(response, HttpStatus.OK);
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

