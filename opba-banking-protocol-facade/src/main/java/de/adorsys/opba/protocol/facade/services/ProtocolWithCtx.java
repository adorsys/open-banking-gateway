package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Selected protocol with associated service context.
 * @param <A> Protocol action
 * @param <I> Served request
 */
@Getter
@RequiredArgsConstructor
public class ProtocolWithCtx<A, I> {
    private final A protocol;
    private final ServiceContext<I> serviceContext;
}
