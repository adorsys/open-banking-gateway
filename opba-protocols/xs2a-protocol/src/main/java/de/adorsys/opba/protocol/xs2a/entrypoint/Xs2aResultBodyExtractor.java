package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AccountReport;
import de.adorsys.opba.protocol.api.dto.result.body.Paging;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionDetailsBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.RemittanceInformationStructured;
import de.adorsys.xs2a.adapter.api.model.Transactions;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return mapper.map((TransactionsResponse200Json) result.getResult(), context);
    }

    public SinglePaymentBody extractSinglePaymentBody(ProcessResponse result) {
        return mapper.map((PaymentInitiationJson) result.getResult());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface Xs2aToFacadeMapper {
        AccountListBody map(AccountList accountList);

        TransactionsResponseBody map(TransactionsResponse200Json transactions, ServiceContext<ListTransactionsRequest> context);

        TransactionDetailsBody map(Transactions transactions);

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

        @AfterMapping
        default void update(@MappingTarget TransactionsResponseBody.TransactionsResponseBodyBuilder transactionsResponseBody,
                            TransactionsResponse200Json transactions,
                            ServiceContext<ListTransactionsRequest> context) {

            List<Transactions> transactionsList = transactions.getTransactions().getBooked();
            transactionsList.addAll(transactions.getTransactions().getPending());

            Paging paging = Paging.builder()
                    .page(context.getRequest().getPage())
                    .perPage(context.getRequest().getPerPage())
                    .pageCount((int) Math.ceil(transactionsList.size() / context.getRequest().getPerPage()))
                    .totalCount(transactionsList.size())
                    .build();

            Xs2aTransactionsPaginator paginator = new Xs2aTransactionsPaginator();
            transactionsList = paginator.getTransactionsPage(transactionsList, paging);

            TransactionListBody bookedTransactions = new TransactionListBody();
            TransactionListBody pendingTransactions = new TransactionListBody();

            transactionsList
                    .stream()
                    .forEach(transaction -> {
                        if (transactions.getTransactions().getBooked().contains(transaction)) {
                            bookedTransactions.add(map(transaction));
                        } else {
                            pendingTransactions.add(map(transaction));
                        }
                    });

            transactionsResponseBody
                    .transactions(AccountReport.builder()
                            .pending(pendingTransactions)
                            .booked(bookedTransactions)
                            .build())
                    .paging(paging);
        }
    }
}
