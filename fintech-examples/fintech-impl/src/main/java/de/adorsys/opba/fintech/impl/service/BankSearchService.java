package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.BankProfile;
import de.adorsys.opba.fintech.api.model.InlineResponse2001;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class BankSearchService {
    public InlineResponse2001 searchBank(String keyword, Integer start, Integer max) {
        log.info("keyword:" + keyword.toString());
        log.info("start:" + start.toString());
        log.info("max:" + max.toString());
        InlineResponse2001 response = new InlineResponse2001();

        List<BankProfile> list = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(max.toString()); i++) {
            BankProfile bp = new BankProfile();
            bp.setBankName(keyword + "_Affe");
            bp.setBic("" + i);
            String[] serviceNames = {"listAccounts", "listTransactions", "initiatePayment"};
            List<String> services = Arrays.asList(serviceNames);
            bp.setServices(services);
            list.add(bp);
        }

        response.setBankProfile(list);
        return response;
    }
}
