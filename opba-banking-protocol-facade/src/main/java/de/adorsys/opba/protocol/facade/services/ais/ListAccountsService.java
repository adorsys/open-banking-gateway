package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ResultHandler;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_ACCOUNTS;

@Service
public class ListAccountsService extends FacadeService<ListAccountsRequest, AccountList, ListAccounts> {

    public ListAccountsService(
            Map<String, ? extends ListAccounts> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ResultHandler handler) {
        super(LIST_ACCOUNTS, actionProviders, selector, provider, handler);
    }
}
