package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.BankDescriptor;
import de.adorsys.opba.fintech.api.model.BankProfile;
import de.adorsys.opba.tpp.bankserach.api.model.BankProfileDescriptor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Mapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.bankserach.api.model.BankDescriptor tppBankDescriptor) {
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
        return fintechBankProfile;
    }
}
