package de.adorsys.opba.tppbankingapi.domain;

import de.adorsys.opba.tppbankingapi.domain.entity.Bank;
import de.adorsys.opba.tppbankingapi.domain.entity.BankProfile;
import de.adorsys.opba.tppbankingapi.search.model.BankDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.BankProfileDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappersTest {

    private static final Bank TEST_BANK = new Bank(1L, "dd624199-d071-4c95-b554-179b0e92c707", "Commerzbank", "COBADEFFXXX", "36040039");

    @Test
    void bankSearchMapperTest() {
        BankDescriptor bankDescriptor = Bank.TO_BANK_DESCRIPTOR.map(TEST_BANK);

        assertEquals(bankDescriptor.getBankName(), TEST_BANK.getName());
        assertEquals(bankDescriptor.getBankCode(), TEST_BANK.getBankCode());
        assertEquals(bankDescriptor.getBic(), TEST_BANK.getBic());
        assertEquals(bankDescriptor.getUuid(), TEST_BANK.getUuid());

    }

    @Test
    void bankProfileMapperTest() {
        BankProfile bankProfile = new BankProfile();
        bankProfile.setBank(TEST_BANK);
        bankProfile.setServices(Arrays.asList(Service.ACCOUNTS, Service.TRANSACTIONS, Service.PAYMENT));

        BankProfileDescriptor bankProfileDescriptor = BankProfile.TO_BANK_PROFILE_DESCRIPTOR.map(bankProfile);

        assertEquals(bankProfileDescriptor.getBankName(), bankProfile.getBank().getName());
        assertEquals(bankProfileDescriptor.getBic(), bankProfile.getBank().getBic());
        assertEquals(bankProfileDescriptor.getBankUuid(), bankProfile.getBank().getUuid());
        List<String> services = bankProfile.getServices().stream().map(Service::getCode).collect(Collectors.toList());
        assertEquals(bankProfileDescriptor.getServiceList(), services);
    }

}
