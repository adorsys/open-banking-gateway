package de.adorsys.opba.protocol.hbci.entrypoint;

import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListDetailBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListAccountsResult;
import de.adorsys.opba.protocol.hbci.service.protocol.ais.dto.AisListTransactionsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;

/**
 * Extracts HBCI result from ASPSP response and does initial translation to Banking protocol facade native object
 * for transactions or accounts list.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HbciResultBodyExtractor {

    private final HbciAccountsToFacadeMapper mapper;
    private final HbciTransactionsToFacadeMapper transactionsToFacadeMapper;

    public AccountListBody extractAccountList(ProcessResponse result) {
        AisListAccountsResult accountsResult = (AisListAccountsResult) result.getResult();
        return mapper.map(accountsResult);
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result) {
        AisListTransactionsResult transactionsResult = (AisListTransactionsResult) result.getResult();
        return transactionsToFacadeMapper.map(transactionsResult);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE, uses = {HbciToAccountBodyMapper.class})
    public interface HbciAccountsToFacadeMapper {

        AccountListBody map(AisListAccountsResult accountList);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface HbciToAccountBodyMapper {

        @Mapping(
                expression = "java(com.google.common.base.Strings.isNullOrEmpty(account.getIban()) ? account.getAccountNumber() : account.getIban())",
                target = "resourceId"
        )
        @Mapping(source = "accountNumber", target = "bban")
        AccountListDetailBody map(BankAccount account);
    }
}
