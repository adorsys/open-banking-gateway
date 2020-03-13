package de.adorsys.opba.protocol.facade.services;

public interface CauseMapper<F, T> {

    T map(F from);
}
