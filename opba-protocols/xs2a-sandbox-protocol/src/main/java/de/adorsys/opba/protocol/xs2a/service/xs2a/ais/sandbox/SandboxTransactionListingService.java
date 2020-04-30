package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox;

import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.AccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.TransactionListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aSandboxTransactionListing")
public class SandboxTransactionListingService extends TransactionListingService {

    private final NoResponseAccountListingService accountListingService;

    public SandboxTransactionListingService(
            ApplicationEventPublisher eventPublisher,
            TransactionListingService.Extractor extractor,
            AccountListingService.Extractor accountListExtractor,
            Xs2aValidator validator,
            AccountInformationService ais) {
        super(eventPublisher, extractor, validator, ais);
        this.accountListingService = new NoResponseAccountListingService(accountListExtractor, validator, ais);
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        // XS2A sandbox quirk... we need to list accounts before listing transactions
        accountListingService.execute(execution);
        super.doRealExecution(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        // XS2A sandbox quirk... we need to list accounts before listing transactions
        accountListingService.execute(execution);
        super.doMockedExecution(execution, context);
    }
}
