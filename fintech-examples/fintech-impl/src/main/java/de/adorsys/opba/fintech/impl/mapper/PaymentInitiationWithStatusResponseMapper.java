package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.PaymentInitiationWithStatusResponse;
import de.adorsys.opba.tpp.pis.api.model.generated.PaymentInformationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Mapper(implementationPackage = "de.adorsys.opba.fintech.impl.mapper.generated")
public abstract class PaymentInitiationWithStatusResponseMapper {

    @Mapping(source = "createdAt", target = "initiationDate")
    public abstract PaymentInitiationWithStatusResponse mapFromTppToFintech(PaymentInformationResponse response);

    public LocalDate mapDateFromOffsetTime(OffsetDateTime dateTime) {
        if (null == dateTime) {
            return null;
        }

        return dateTime.toLocalDate();
    }
}
