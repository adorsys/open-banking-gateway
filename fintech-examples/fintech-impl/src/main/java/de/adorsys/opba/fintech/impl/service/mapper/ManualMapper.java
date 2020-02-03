package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.AccountReport;
import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileDescriptor;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class ManualMapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor tppBankDescriptor) {
        return Mappers.getMapper(BankDescriptorMapper.class).map(tppBankDescriptor);
    }

    public static BankProfile fromTppToFintech(BankProfileDescriptor tppBankProfile) {
        return Mappers.getMapper(BankProfileDescriptorMapper.class).map(tppBankProfile);
    }

    public static AccountList fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountList tppAccountList) {
        return Mappers.getMapper(AccountListMapper.class).map(tppAccountList);
    }

    public static AccountReport fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountReport tppAccountReport) {
        return Mappers.getMapper(AccountReportMapper.class).map(tppAccountReport);
    }
}
