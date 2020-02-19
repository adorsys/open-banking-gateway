package de.adorsys.opba.protocol.api.dto.result.body;

import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountDetails;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * This mapper will be replaced with generic mapper soon
 */
@Slf4j
public class FacadeToProtocolMapper {

    public AccountList getProtocolEntity(AccountListBody facadeEntity) {
        log.debug("FacadeToProtocolMapper mapping AccountListBody -> AccountList");

        AccountList protocoalEntity = new AccountList();
        protocoalEntity.setAccounts(getProtocolEntity(facadeEntity.getAccountListDetails()));
        return protocoalEntity;
    }

    public List<AccountDetails> getProtocolEntity(List<AccountListDetailBody> facadeList) {
        List<AccountDetails> protocoalList = new ArrayList<>();
        facadeList.stream().forEach(facadeEntity -> protocoalList.add(getProtocolEntity(facadeEntity)));
        return protocoalList;
    }

    public AccountDetails getProtocolEntity(AccountListDetailBody facadeEntity) {
        AccountDetails protocoalEntity = new AccountDetails();
        protocoalEntity.setBban(facadeEntity.getBban());
        protocoalEntity.setBic(facadeEntity.getBic());
        protocoalEntity.setCashAccountType(facadeEntity.getCashAccountType());
        protocoalEntity.setCurrency(facadeEntity.getCurrency());
        protocoalEntity.setMaskedPan(facadeEntity.getMaskedPan());
        protocoalEntity.setIban(facadeEntity.getIban());
        protocoalEntity.setMsisdn(facadeEntity.getMsisdn());
        protocoalEntity.setName(facadeEntity.getName());
        protocoalEntity.setPan(facadeEntity.getPan());
        protocoalEntity.setProduct(facadeEntity.getProduct());
        protocoalEntity.setStatus(AccountStatus.fromValue(facadeEntity.getStatus()));
        // TODO a lot is missing
        return protocoalEntity;
    }
}
