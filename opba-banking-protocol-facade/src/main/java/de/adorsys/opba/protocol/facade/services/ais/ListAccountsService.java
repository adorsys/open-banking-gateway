package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.facade.services.RoutingService;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.LIST_ACCOUNTS;

@Service
@RequiredArgsConstructor
public class ListAccountsService {

    // bean name - bean-impl.
    private final Map<String, ? extends ListAccounts> accountListProviders;
    private final RoutingService router;

    public CompletableFuture<Result<AccountList>> list(ListAccountsRequest request) {
        return router.protocolFor(request.getBankID(), LIST_ACCOUNTS, accountListProviders).list(request);
    }
}
