package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import de.adorsys.opba.fintech.impl.service.entities.ContextInformation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BankSearchService {

    @SneakyThrows
    public InlineResponse2001 searchBank(ContextInformation contextInformation, String keyword, Integer start, Integer max) {
        InlineResponse2001 response = new InlineResponse2001();

        /*
        TppBankSearchApi tppBankSearchApi = new TppBankSearchApi();
        BankSearchResponse bankSearchResponse = tppBankSearchApi.bankSearchGET(contextInformation.getFintechID(), contextInformation.getXRequestID(), keyword, start, max);

        List<BankDescriptor>outList = new ArrayList<>();

        bankSearchResponse.getBankDescriptor().forEach(r -> {
            BankDescriptor bankDescriptor = new BankDescriptor();
            bankDescriptor.setBankCode(r.);
        }


         List<BankDescriptor> bankDescriptor = bankSearchResponse.getBankDescriptor();
         */
        return response;
    }
}
