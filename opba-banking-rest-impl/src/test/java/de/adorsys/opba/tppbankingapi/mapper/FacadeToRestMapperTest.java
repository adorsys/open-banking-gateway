package de.adorsys.opba.tppbankingapi.mapper;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FacadeToRestMapperTest {
    private static final String IBAN = "DE122342343243";
    private static final String DELETED = "deleted";

    @Test
    public void testMapping() {
        AccountListDetailBody account = AccountListDetailBody.builder()
                .iban(IBAN)
                .status(DELETED)
                .build();

        List<AccountListDetailBody> accountDetails = new ArrayList<>();
        accountDetails.add(account);
        AccountListBody facadeEntity = AccountListBody.builder().accounts(accountDetails).build();

        AccountList restEntity = Mappers.getMapper(AccountListFacadeToRestMapper.class).mapFromFacadeToRest(facadeEntity);
        assertEquals(IBAN, restEntity.getAccounts().get(0).getIban());
        assertEquals(DELETED, restEntity.getAccounts().get(0).getStatus().name());
    }
}
