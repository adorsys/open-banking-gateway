package de.adorsys.opba.protocol.xs2a.service;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper migth be replaced by generic mapper in next step
 */
@Slf4j
public class XS2aToFacadeMapper {
    public AccountListBody getFacadeEntity(AccountListHolder xs2aEntity) {
        log.debug("XS2aToFacadeMapper mapping AccountListHolder -> AccountListBody");
        List<AccountListDetailBody> facadeEntitiy = new ArrayList<>();
        xs2aEntity.getAccounts().stream().forEach(xs2aAccountDetail -> facadeEntitiy.add(getFacadeEntity(xs2aAccountDetail)));
        return AccountListBody.builder().accountListDetails(facadeEntitiy).build();
    }

    public AccountListDetailBody getFacadeEntity(de.adorsys.xs2a.adapter.service.model.AccountDetails xs2aEntity) {
        return AccountListDetailBody.builder()
                .bban(xs2aEntity.getBban())
                .bic(xs2aEntity.getBic())
                .cashAccountType(xs2aEntity.getCashAccountType().getValue())
                .currency(xs2aEntity.getCurrency())
                .iban(xs2aEntity.getIban())
                .maskedPan(xs2aEntity.getMaskedPan())
                .msisdn(xs2aEntity.getMsisdn())
                .pan(xs2aEntity.getPan())
                .product(xs2aEntity.getProduct())
                .resourceId(xs2aEntity.getResourceId())
                .status(xs2aEntity.getStatus().getValue())
                .build();
    }

}
