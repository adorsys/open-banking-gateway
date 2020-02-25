package de.adorsys.opba.protocol.xs2a.service.xs2a.ais.sandbox;

import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.AccountListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.ais.TransactionListingService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aSandboxTransactionListing")
public class SandboxTransactionListingService extends TransactionListingService {

    private final AccountListingService accountListingService;

    public SandboxTransactionListingService(
            TransactionListingService.Extractor extractor,
            Xs2aValidator validator,
            AccountInformationService ais,
            AccountListingService accountListingService) {
        super(extractor, validator, ais);
        this.accountListingService = accountListingService;
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        // XS2A sandbox quirk...
        accountListingService.execute(execution);
        super.doRealExecution(execution, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        // XS2A sandbox quirk...
        accountListingService.execute(execution);
        super.doMockedExecution(execution, context);
    }
}
