package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import de.adorsys.opba.fintech.api.model.InlineResponse2002;
import de.adorsys.opba.fintech.impl.service.entities.ContextInformation;
import de.adorsys.opba.fintech.impl.service.mapper.Mapper;
import de.adorsys.opba.tpp.bankserach.api.model.BankSearchResponse;
import de.adorsys.opba.tpp.bankserach.api.resource.TppBankSearchApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
@Slf4j
public class BankSearchService {

    @Autowired
    private TppBankSearchApi tppBankSearchApi;

    @SneakyThrows
    public InlineResponse2001 searchBank(ContextInformation contextInformation, String keyword, Integer start, Integer max) {
        BankSearchResponse bankSearchResponse = tppBankSearchApi.bankSearchGET(contextInformation.getFintechID(), contextInformation.getXRequestID(), keyword, start, max);
        InlineResponse2001 inlineResponse2001 =
                new InlineResponse2001().bankDescriptor(bankSearchResponse.getBankDescriptor().stream().map(bankDescriptor -> Mapper.fromTppToFintech(bankDescriptor)).collect(Collectors.toList()));
        inlineResponse2001.setKeyword(bankSearchResponse.getKeyword());
        inlineResponse2001.setMax(bankSearchResponse.getMax());
        inlineResponse2001.setStart(bankSearchResponse.getStart());
        inlineResponse2001.setTotal(bankSearchResponse.getTotal());
        return inlineResponse2001;
    }

    @SneakyThrows
    public InlineResponse2002 searchBankProfile(ContextInformation contextInformation, String bankId) {
        return new InlineResponse2002().bankProfile(
                Mapper.fromTppToFintech(tppBankSearchApi.bankProfileGET(contextInformation.getFintechID(), contextInformation.getXRequestID(), bankId).getBankProfileDescriptor()));
    }
}
