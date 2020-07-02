package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.PaymentInitiationWithStatusResponse;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.fintech.impl.mapper.generated")
public abstract class PaymentInitiationWithStatusResponseMapper {
    public abstract PaymentInitiationWithStatusResponse mapFromTppToFintech(de.adorsys.opba.tpp.pis.api.model.generated.PaymentInitiationWithStatusResponse response);
}
