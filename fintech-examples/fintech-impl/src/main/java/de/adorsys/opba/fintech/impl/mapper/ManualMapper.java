package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileDescriptor;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class ManualMapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor tppBankDescriptor) {
        return Mappers.getMapper(BankDescriptorMapper.class).mapFromTppToFintech(tppBankDescriptor);
    }

    public static BankProfile fromTppToFintech(BankProfileDescriptor tppBankProfile) {
        return Mappers.getMapper(BankProfileDescriptorMapper.class).mapFromTppToFintech(tppBankProfile);
    }

    public static TransactionsResponse fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse transactionsResponse) {
        return Mappers.getMapper(TransactionsResponseMapper.class).mapFromTppToFintech(transactionsResponse);
    }
}
