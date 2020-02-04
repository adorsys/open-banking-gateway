package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.fintech.api.model.generated.TransactionsResponse;
import de.adorsys.opba.fintech.impl.mapper.generated.AccountListMapper;
import de.adorsys.opba.fintech.impl.mapper.generated.BankDescriptorMapper;
import de.adorsys.opba.fintech.impl.mapper.generated.BankProfileDescriptorMapper;
import de.adorsys.opba.fintech.impl.mapper.generated.TransactionsResponseMapper;
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

    public static AccountList fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountList tppAccountList) {
        return Mappers.getMapper(AccountListMapper.class).mapFromTppToFintech(tppAccountList);
    }

    public static TransactionsResponse fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse transactionsResponse) {
        return Mappers.getMapper(TransactionsResponseMapper.class).mapFromTppToFintech(transactionsResponse);
    }
}
