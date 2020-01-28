package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.tpp.bankserach.api.model.generated.BankProfileDescriptor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Mapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.bankserach.api.model.generated.BankDescriptor tppBankDescriptor) {
        BankDescriptor fintechBankDescrptor = new BankDescriptor();
        fintechBankDescrptor.setBankCode(tppBankDescriptor.getBankCode());
        fintechBankDescrptor.setBankName(tppBankDescriptor.getBankName());
        fintechBankDescrptor.setBic(tppBankDescriptor.getBic());
        fintechBankDescrptor.setUuid(tppBankDescriptor.getUuid());
        return fintechBankDescrptor;
    }

    public static BankProfile fromTppToFintech(BankProfileDescriptor tppBankProfile) {
        BankProfile fintechBankProfile = new BankProfile();
        fintechBankProfile.setBic(tppBankProfile.getBic());
        fintechBankProfile.setBankName(tppBankProfile.getBankName());
        fintechBankProfile.setServices(tppBankProfile.getServiceList());
        fintechBankProfile.setBankId(tppBankProfile.getBankUuid());
        return fintechBankProfile;
    }
}
