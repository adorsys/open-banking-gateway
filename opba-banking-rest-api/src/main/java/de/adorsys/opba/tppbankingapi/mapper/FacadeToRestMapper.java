package de.adorsys.opba.tppbankingapi.mapper;

public interface FacadeToRestMapper<R, F> {
    R mapFromFacadeToRest(F facadeEntity);
}
