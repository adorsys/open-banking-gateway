package de.adorsys.opba.fintech.impl.mapper.generated;

import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import org.mapstruct.Mapper;

@Mapper
public interface BankDescriptorMapper {
    BankDescriptor mapFromTppToFintech(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor bankDescriptor);
}
