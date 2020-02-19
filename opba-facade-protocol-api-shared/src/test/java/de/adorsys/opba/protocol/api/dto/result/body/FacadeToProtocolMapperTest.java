package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

public class FacadeToProtocolMapperTest {
    static String IBAN = "DE122342343243";

    @Test
    public void testMapping() {
        AccountListBody facadeEntity = null;
        {
            AccountListDetailBody account = AccountListDetailBody.builder()
                    .iban(IBAN)
                    .build();

            List<AccountListDetailBody> accountDetails = new ArrayList<>();
            accountDetails.add(account);
            facadeEntity = AccountListBody.builder()
                    .accounts(accountDetails).build();
        }
        AccountList protocolEntity = Mappers.getMapper(FacadeToProtocolMapper.class).mapFromFacadeToProtocol(facadeEntity);
        Assertions.assertEquals(IBAN, protocolEntity.getAccounts().get(0).getIban());
    }
}
