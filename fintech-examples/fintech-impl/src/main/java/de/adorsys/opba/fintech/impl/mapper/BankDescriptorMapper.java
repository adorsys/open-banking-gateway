package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import org.mapstruct.Mapper;

@Mapper(implementationPackage = "de.adorsys.opba.fintech.impl.mapper.generated")
public interface BankDescriptorMapper {
    BankDescriptor mapFromTppToFintech(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor bankDescriptor);
}
