package de.adorsys.opba.protocol.xs2a.entrypoint;

import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ProcessResponse;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Service
@RequiredArgsConstructor
public class Xs2aResultBodyExtractor {

    private final Xs2aToFacadeMapper mapper;

    public AccountListBody extractAccountList(ProcessResponse result) {
        return mapper.map((AccountListHolder) result.getResult());
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result) {
        return mapper.map((TransactionsReport) result.getResult());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface Xs2aToFacadeMapper {
        AccountListBody map(AccountListHolder accountList);
        TransactionsResponseBody map(TransactionsReport transactions);
    }
}
