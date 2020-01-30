package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ListAccountsService {

    // bean name - bean-impl.
    private final Map<String, ? extends ListAccounts> accountListProviders;

    public CompletableFuture<Result<AccountList>> list(ListAccountsRequest request) {
        // FIXME - xs2aListAccounts - This is a stub - waiting for 1st subtask (OBG-209):
        return accountListProviders.get("xs2aListAccounts").list(request);
    }
}
