package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.BankDescriptor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Springboot starter class is not an utility class
public class Mapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.bankserach.api.model.BankDescriptor tppBankDescriptor) {
        BankDescriptor fintechBankDescrptor = new BankDescriptor();
        fintechBankDescrptor.setBankCode(tppBankDescriptor.getBankCode());
        fintechBankDescrptor.setBankName(tppBankDescriptor.getBankName());
        fintechBankDescrptor.setBic(tppBankDescriptor.getBic());
        fintechBankDescrptor.setUuid(tppBankDescriptor.getUuid());
        return fintechBankDescrptor;
    }
}
