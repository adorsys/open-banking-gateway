package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountDetails;
import de.adorsys.opba.fintech.api.model.generated.AccountList;
import de.adorsys.opba.fintech.api.model.generated.AccountReport;
import de.adorsys.opba.fintech.api.model.generated.AccountStatus;
import de.adorsys.opba.fintech.api.model.generated.BankDescriptor;
import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.fintech.api.model.generated.ReportExchangeRate;
import de.adorsys.opba.fintech.api.model.generated.ReportExchangeRateList;
import de.adorsys.opba.fintech.api.model.generated.TransactionDetails;
import de.adorsys.opba.fintech.api.model.generated.TransactionList;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileDescriptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Mapper {
    public static BankDescriptor fromTppToFintech(de.adorsys.opba.tpp.banksearch.api.model.generated.BankDescriptor tppBankDescriptor) {
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

    public static AccountStatus fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountStatus tppAccountStatus) {
        if (tppAccountStatus == null) {
            return null;
        }
        return AccountStatus.fromValue(tppAccountStatus.toString());
    }

    public static AccountList fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountList tppAccountList) {
        AccountList accountList = new AccountList();
        final List<AccountDetails> accountDetails = new ArrayList<>();
        tppAccountList.getAccounts().forEach(tppAccountDetail -> {
            accountDetails.add(fromTppToFintech(tppAccountDetail));
        });
        accountList.setAccounts(accountDetails);
        return accountList;
    }

    private static AccountDetails fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountDetails tppAccountDetail) {
        AccountDetails fintechAccountDetails = new AccountDetails();
        fintechAccountDetails.setBban(tppAccountDetail.getBban());
        fintechAccountDetails.setBic(tppAccountDetail.getBic());
        fintechAccountDetails.setCashAccountType(tppAccountDetail.getCashAccountType());
        fintechAccountDetails.setCurrency(tppAccountDetail.getCurrency());
        fintechAccountDetails.setLinkedAccounts(tppAccountDetail.getLinkedAccounts());
        fintechAccountDetails.setMaskedPan(tppAccountDetail.getMaskedPan());
        fintechAccountDetails.setIban(tppAccountDetail.getIban());
        fintechAccountDetails.setMsisdn(tppAccountDetail.getMsisdn());
        fintechAccountDetails.setName(tppAccountDetail.getName());
        fintechAccountDetails.setOwnerAddress(fintechAccountDetails.getOwnerAddress());
        // TODO Links not handled yet
        // fintechAccountDetails.setLinks(tppAccountDetail.getLinks());
        fintechAccountDetails.setPan(tppAccountDetail.getPan());
        fintechAccountDetails.setResourceId(tppAccountDetail.getResourceId());
        fintechAccountDetails.setDetails(tppAccountDetail.getDetails());
        fintechAccountDetails.setProduct(fintechAccountDetails.getPan());
        fintechAccountDetails.setOwnerName(tppAccountDetail.getOwnerName());
        fintechAccountDetails.setStatus(fromTppToFintech(tppAccountDetail.getStatus()));
        return fintechAccountDetails;
    }

    public static AccountReport fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.AccountReport tppAccountReport) {
        AccountReport fintechAccountReport = new AccountReport();
        fintechAccountReport.setBooked(mapFromTppToFintech(tppAccountReport.getBooked()));
        fintechAccountReport.setPending(mapFromTppToFintech(tppAccountReport.getPending()));
        return fintechAccountReport;
    }

    private static TransactionList mapFromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionList tppTransactionList) {
        TransactionList finTechTransactionList = new TransactionList();
        tppTransactionList.stream().forEach(tppTransactionDetail -> {
            finTechTransactionList.add(fromTppToFintech(tppTransactionDetail));
        });
        return finTechTransactionList;
    }

    private static TransactionDetails fromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails tppTransactionDetails) {
        TransactionDetails fintechTransactionDetails = new TransactionDetails();
        fintechTransactionDetails.setAdditionalInformation(tppTransactionDetails.getAdditionalInformation());
        fintechTransactionDetails.setBankTransactionCode(tppTransactionDetails.getBankTransactionCode());
        fintechTransactionDetails.setBookingDate(tppTransactionDetails.getBookingDate());
        fintechTransactionDetails.setCheckId(tppTransactionDetails.getCheckId());
        // TODO
        // fintechTransactionDetails.setCreditorAccount(tppTransactionDetails.getCreditorAccount());
        fintechTransactionDetails.setCreditorName(tppTransactionDetails.getCreditorName());
        fintechTransactionDetails.setCurrencyExchange(mapFromTppToFinTech(tppTransactionDetails.getCurrencyExchange()));

        // TODO
        // and so on
        return fintechTransactionDetails;
    }

    private static ReportExchangeRateList mapFromTppToFinTech(de.adorsys.opba.tpp.ais.api.model.generated.ReportExchangeRateList tppReportExchangeRates) {
        ReportExchangeRateList finTechReportExchangeRateList = new ReportExchangeRateList();
        tppReportExchangeRates.stream().forEach(tppReportExchangeRate ->
                finTechReportExchangeRateList.add(mapFromTppToFintech(tppReportExchangeRate))
        );
        return finTechReportExchangeRateList;
    }

    private static ReportExchangeRate mapFromTppToFintech(de.adorsys.opba.tpp.ais.api.model.generated.ReportExchangeRate tppReportExchangeRate) {
        ReportExchangeRate finTechReportExchangeRate = new ReportExchangeRate();
        finTechReportExchangeRate.setContractIdentification(tppReportExchangeRate.getContractIdentification());
        // TODO and so on
        return finTechReportExchangeRate;
    }
}
