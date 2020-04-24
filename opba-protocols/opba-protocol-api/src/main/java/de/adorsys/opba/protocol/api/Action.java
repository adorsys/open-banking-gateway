package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;

import java.util.concurrent.CompletableFuture;

/**
 * Generic action interface that describe what protocol can do.
 * @param <I> Users' request type
 * @param <O> Result body type
 */
public interface Action<I, O> {

    /**
     * Execute associated action using the input.
     * @param serviceContext Request context (Request ID, Bank ID, password, redirect URLs, etc.) and associated services (Encryption, Consent access, etc.)
     * @return The outcome of the executed action that will be parsed by Facade.
     */
    CompletableFuture<Result<O>> execute(ServiceContext<I> serviceContext);
}
