package de.adorsys.opba.tppbankingapi.mapper;

public interface FacadeToRestMapperBase<R,F> {
    R mapFromFacadeToRest(F facadeEntity);
}
