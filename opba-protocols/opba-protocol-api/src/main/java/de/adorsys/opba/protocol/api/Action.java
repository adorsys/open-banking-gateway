package de.adorsys.opba.protocol.api;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;

import java.util.concurrent.CompletableFuture;

public interface Action<I, O> {

    CompletableFuture<Result<O>> execute(ServiceContext<I> serviceContext);
}
