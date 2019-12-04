package de.adorsys.opba.core.protocol.service.xs2a;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.core.protocol.service.xs2a.context.TransactionListXs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;

@Service
public class ContextFactory {

    public Map<String, Object> createXs2aContext() {
        return new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, createContext()));
    }

    public Map<String, Object> createXs2aContextForTx() {
        return new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, createContextForTx()));
    }

    private Xs2aContext createContext() {
        Xs2aContext context = new Xs2aContext();
        context.setPsuId("anton.brueckner");
        context.setRequestId("2f77a125-aa7a-45c0-b414-cea25a116035");
        context.setGatewayAspspId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46");
        context.setPsuIpAddress("1.1.1.1");

        return context;
    }

    private TransactionListXs2aContext createContextForTx() {
        TransactionListXs2aContext context = new TransactionListXs2aContext();
        context.setPsuId("anton.brueckner");
        context.setRequestId("2f77a125-aa7a-45c0-b414-cea25a116035");
        context.setGatewayAspspId("53c47f54-b9a4-465a-8f77-bc6cd5f0cf46");
        context.setPsuIpAddress("1.1.1.1");
        context.setResourceId("d2ZqA1ObSS0qfd4geGdAFw"); // account ID
        context.setCurrency("EUR");
        context.setIban("DE80760700240271232400");
        return context;
    }
}
