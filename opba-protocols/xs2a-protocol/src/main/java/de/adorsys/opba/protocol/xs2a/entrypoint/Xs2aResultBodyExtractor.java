package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.RemittanceInformationStructured;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Extracts Xs2a result from ASPSP response and does initial translation to Banking protocol facade native object
 * for transactions or accounts list.
 */
@Service
@RequiredArgsConstructor
public class Xs2aResultBodyExtractor {

    private final Xs2aToFacadeMapper mapper;

    public AccountListBody extractAccountList(ProcessResponse result) {
        return mapper.map((AccountList) result.getResult());
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result, ServiceContext<ListTransactionsRequest> context) {
        return mapper.map((TransactionsResponse200Json) result.getResult());
    }

    public SinglePaymentBody extractSinglePaymentBody(ProcessResponse result) {
        return mapper.map((PaymentInitiationJson) result.getResult());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface Xs2aToFacadeMapper {
        AccountListBody map(AccountList accountList);

        TransactionsResponseBody map(TransactionsResponse200Json transactions);

        @Mapping(source = "singlePaymentInitiationBody.creditorAddress.townName", target = "creditorAddress.city")
        SinglePaymentBody map(PaymentInitiationJson singlePaymentInitiationBody);

        default String map(RemittanceInformationStructured value) {
            if (null == value) {
                return null;
            }

            StringBuilder builder = new StringBuilder();
            append(builder, value.getReferenceType());
            append(builder, value.getReferenceIssuer());
            append(builder, value.getReference());
            return builder.toString();
        }

        default void append(StringBuilder builder, String referenceType) {
            if (Strings.isNullOrEmpty(referenceType)) {
                return;
            }

            builder.append(referenceType);
            builder.append(":");
        }
    }
}
