package de.adorsys.opba.db.domain;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.domain.entity.BankAction;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.tppbankingapi.search.model.generated.BankDescriptor;
import de.adorsys.opba.tppbankingapi.search.model.generated.BankProfileDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappersTest {

    private static final Bank TEST_BANK = new Bank(
            1L,
            UUID.fromString("dd624199-d071-4c95-b554-179b0e92c707"),
            "Commerzbank",
            "COBADEFFXXX",
            "36040039",
            true,
            Collections.emptyList(),
            Collections.emptyList()
    );

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
        Map<ProtocolAction, BankAction> actionMap = new HashMap<>();
        actionMap.put(ProtocolAction.LIST_TRANSACTIONS, new BankAction());
        actionMap.put(ProtocolAction.LIST_ACCOUNTS, new BankAction());
        actionMap.put(ProtocolAction.SINGLE_PAYMENT, new BankAction());
        bankProfile.setActions(actionMap);

        BankProfileDescriptor bankProfileDescriptor = BankProfile.TO_BANK_PROFILE_DESCRIPTOR.map(bankProfile);

        assertEquals(bankProfileDescriptor.getBankName(), bankProfile.getBank().getName());
        assertEquals(bankProfileDescriptor.getBic(), bankProfile.getBank().getBic());
        assertEquals(bankProfileDescriptor.getBankUuid(), bankProfile.getBank().getUuid().toString());
        List<String> services = bankProfile.getActions().keySet().stream().map(Enum::name).collect(Collectors.toList());
        assertEquals(bankProfileDescriptor.getServiceList(), services);
    }

}
