package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.impl.config.TppAisClient;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@NoArgsConstructor
public class AccountService {
    @Value("real.tpp.ais")
    String realTppAISString;
    Boolean mockTppAIS = realTppAISString != null && realTppAISString.equalsIgnoreCase("true") ? false : true;

    // Todo with RequiredArgsConstructor

    @Autowired
    TppAisClient tppAisClient;

    public InlineResponse2003 listAccounts(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId) {

        AccountList accountList = null;
        if (!mockTppAIS) {
            accountList = tppAisClient.getAccounts(
                    contextInformation.getFintechID(),
                    sessionEntity.getLoginUserName(),
                    "okUrl",
                    "notOkUrl",
                    contextInformation.getXRequestID(),
                    bankId,
                    null).getBody();
        }
        if (mockTppAIS) {
            accountList = new TppListAccountsMock().getAccountList();
        }
        InlineResponse2003 response = new InlineResponse2003();
        response.setAccountList(ManualMapper.fromTppToFintech(accountList));
        return response;
    }
}
