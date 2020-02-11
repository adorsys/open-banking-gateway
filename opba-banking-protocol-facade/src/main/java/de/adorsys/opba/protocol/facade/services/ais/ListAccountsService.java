package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_ACCOUNTS;

@Service
public class ListAccountsService extends FacadeService<ListAccountsRequest, AccountListBody, ListAccounts> {

    public ListAccountsService(
            Map<String, ? extends ListAccounts> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ProtocolResultHandler handler) {
        super(LIST_ACCOUNTS, actionProviders, selector, provider, handler);
    }
}
