package de.adorsys.opba.protocol.api.dto.result.body;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.AssertEquals.assertEquals;

public class FacadeToProtocolMapperTest {
    private static final String IBAN = "DE122342343243";

    @Test
    public void testMapping() {
        AccountListDetailBody account = AccountListDetailBody.builder()
                .iban(IBAN)
                .status(AccountStatus.DELETED.name())
                .build();

        List<AccountListDetailBody> accountDetails = new ArrayList<>();
        accountDetails.add(account);
        AccountListBody facadeEntity = AccountListBody.builder().accounts(accountDetails).build();

        AccountList protocolEntity = Mappers.getMapper(FacadeToProtocolMapper.class).mapFromFacadeToProtocol(facadeEntity);
        assertEquals(IBAN, protocolEntity.getAccounts().get(0).getIban());
        assertEquals(AccountStatus.DELETED.name(), protocolEntity.getAccounts().get(0).getStatus().name());
    }
}
