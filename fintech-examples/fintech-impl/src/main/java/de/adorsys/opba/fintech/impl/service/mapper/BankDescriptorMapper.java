package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import org.mapstruct.Mapper;

@Mapper
public interface BankDescriptorMapper {
    BankDescriptor map(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor bankDescriptor);
}
