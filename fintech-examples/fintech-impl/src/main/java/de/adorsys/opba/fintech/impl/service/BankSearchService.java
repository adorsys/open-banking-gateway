package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import de.adorsys.opba.fintech.impl.service.entities.ContextInformation;
import de.adorsys.opba.fintech.impl.service.mapper.Mapper;
import de.adorsys.opba.tpp.bankserach.api.model.BankSearchResponse;
import de.adorsys.opba.tpp.bankserach.api.resource.TppBankSearchApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
@Slf4j
public class BankSearchService {


    @SneakyThrows
    public InlineResponse2001 searchBank(TppBankSearchApi tppBankSearchApi, ContextInformation contextInformation, String keyword, Integer start, Integer max) {
        BankSearchResponse bankSearchResponse = tppBankSearchApi.bankSearchGET(contextInformation.getFintechID(), contextInformation.getXRequestID(), keyword, start, max);
        return new InlineResponse2001().bankDescriptor(bankSearchResponse.getBankDescriptor().stream().map(bankDescriptor -> Mapper.fromTppToFintech(bankDescriptor)).collect(Collectors.toList()));
    }
}
