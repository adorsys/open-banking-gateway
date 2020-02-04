package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.BankProfile;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountReport;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionList;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionsResponse;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankProfileDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapperTest {
    @Test
    public void testBankProfileDescriptorMapper() {
        final String bankName = "koelner bank";
        final String[] services = {"list tx", "list accounts", "initiate payment"};
        BankProfileDescriptor bankProfileDescriptor = new BankProfileDescriptor();
        bankProfileDescriptor.setBankName(bankName);
        bankProfileDescriptor.setServiceList(Arrays.asList(services));

        BankProfile bankProfile = ManualMapper.fromTppToFintech(bankProfileDescriptor);
        assertEquals(bankName, bankProfile.getBankName());
        assertTrue(bankProfile.getServices().containsAll(Arrays.asList(services)));
    }

    @Test
    public void testTransactionsResponseMapper() {
        final String creditorName = "peter";
        TransactionList transactionList = new TransactionList();
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setCreditorName(creditorName);
        transactionList.add(transactionDetails);
        TransactionsResponse transactionsResponse = new TransactionsResponse();
        AccountReport accountReport = new AccountReport();
        accountReport.setBooked(transactionList);
        transactionsResponse.setTransactions(accountReport);

        de.adorsys.opba.fintech.api.model.generated.TransactionsResponse finTechTransactionsResponse = ManualMapper.fromTppToFintech(transactionsResponse);
        assertEquals(creditorName, finTechTransactionsResponse.getTransactions().getBooked().get(0).getCreditorName());
    }
}
