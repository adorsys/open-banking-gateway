package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Mostly to support HBCI.
 */
@Getter
@RequiredArgsConstructor
public class JacksonMixin<T, C> {

    private final Class<T> type;
    private final Class<C> mixin;
}
