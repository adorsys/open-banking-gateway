package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.db.repository.jpa.BankProtocolRepository;
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
    private final BankProtocolRepository protocolRepository;

    public CompletableFuture<Result<AccountList>> list(ListAccountsRequest request) {
        return protocolRepository.findByBankProfileUuidAndAction(request.getBankID(), ProtocolAction.LIST_ACCOUNTS)
                .map(protocol -> accountListProviders.get(protocol.getProtocolBeanName()))
                .map(action -> action.list(request))
                .orElseThrow(() -> new IllegalStateException("No ais account list bean for " + request.getBankID()));
    }
}
