package de.adorsys.opba.protocol.xs2a.service.xs2a;

import de.adorsys.opba.protocol.xs2a.service.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

// TODO - Drop/refactor as dialog approach is working now
@Service
@Deprecated
public class ContextFactory {

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is a POC hardcoded values
    public Xs2aContext createContext() {
        Xs2aContext context = new Xs2aContext();
        context.setAspspId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46");

        context.setPsuIpAddress("1.1.1.1");
        return context;
    }

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is a POC hardcoded values
    public TransactionListXs2aContext createContextForTx() {
        TransactionListXs2aContext context = new TransactionListXs2aContext();
        context.setAspspId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46");

        context.setPsuIpAddress("1.1.1.1");
        return context;
    }
}
