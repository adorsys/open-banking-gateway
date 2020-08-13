package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2001;
import de.adorsys.opba.fintech.api.model.generated.InlineResponse2002;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankSearchService {

    private final TppBankSearchClient tppBankSearchClient;

    @Autowired
    RestRequestContext restRequestContext;

    @Autowired
    private TppProperties tppProperties;

    @SneakyThrows
    public InlineResponse2001 searchBank(String keyword, Integer start, Integer max) {
        BankSearchResponse bankSearchResponse = tppBankSearchClient.bankSearchGET(tppProperties.getFintechID(), UUID.fromString(restRequestContext.getRequestId()), keyword, start, max).getBody();
        InlineResponse2001 inlineResponse2001 =
                new InlineResponse2001().bankDescriptor(bankSearchResponse.getBankDescriptor().stream().map(
                        bankDescriptor -> ManualMapper.fromTppToFintech(bankDescriptor)).collect(Collectors.toList()));
        inlineResponse2001.setKeyword(bankSearchResponse.getKeyword());
        inlineResponse2001.setMax(bankSearchResponse.getMax());
        inlineResponse2001.setStart(bankSearchResponse.getStart());
        inlineResponse2001.setTotal(bankSearchResponse.getTotal());
        return inlineResponse2001;
    }

    @SneakyThrows
    public InlineResponse2002 searchBankProfile(String bankId) {
        return new InlineResponse2002().bankProfile(
                ManualMapper.fromTppToFintech(tppBankSearchClient.bankProfileGET(tppProperties.getFintechID(),
                        UUID.fromString(restRequestContext.getRequestId()), bankId).getBody().getBankProfileDescriptor()));
    }
}
