package de.adorsys.opba.protocol.xs2a.entrypoint;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.Paging;
import de.adorsys.opba.protocol.api.dto.result.body.Paging.PagingBuilder;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.xs2a.entrypoint.parsers.XmlTransactionsParser;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import de.adorsys.xs2a.adapter.api.model.PaymentInitiationJson;
import de.adorsys.xs2a.adapter.api.model.RemittanceInformationStructured;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

/**
 * Extracts Xs2a result from ASPSP response and does initial translation to Banking protocol facade native object
 * for transactions or accounts list.
 */
@Service
@RequiredArgsConstructor
public class Xs2aResultBodyExtractor {
    private static final String LAST_PAGE_LINK_NAME = "last";
    private static final String PAGE_INDEX_QUERY_PARAMETER_NAME = "pageIndex";

    private final Xs2aToFacadeMapper mapper;
    private final XmlTransactionsParser xmlTransactionsParser;

    public AccountListBody extractAccountList(ProcessResponse result) {
        return mapper.map((AccountList) result.getResult());
    }

    public TransactionsResponseBody extractTransactionsReport(ProcessResponse result, ServiceContext<ListTransactionsRequest> context) {
        if (result.getResult() instanceof TransactionsResponse200Json) {
            return mapper.map((TransactionsResponse200Json) result.getResult(), context);
        }

        var transactionsResponse = xmlTransactionsParser.camtStringToLoadBookingsResponse((String) result.getResult());

        return transactionsResponse.toBuilder()
                .paging(mapper.getPagingBuilderWithRequestPageData(context).build())
                .build();
    }

    public SinglePaymentBody extractSinglePaymentBody(ProcessResponse result) {
        return mapper.map((PaymentInitiationJson) result.getResult());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface Xs2aToFacadeMapper {
        AccountListBody map(AccountList accountList);

        TransactionsResponseBody map(TransactionsResponse200Json transactions, ServiceContext<ListTransactionsRequest> context);

        @AfterMapping
        default void update(@MappingTarget TransactionsResponseBody.TransactionsResponseBodyBuilder responseBodyBuilder,
                            TransactionsResponse200Json transactions,
                            ServiceContext<ListTransactionsRequest> context) {
            var pagingBuilder = getPagingBuilderWithRequestPageData(context);

            var accountReport = transactions.getTransactions();

            if (accountReport != null && accountReport.getLinks() != null) {
                var link = accountReport.getLinks().get(LAST_PAGE_LINK_NAME);

                if (link != null) {
                    var parameters = UriComponentsBuilder.fromUriString(link.getHref()).build().getQueryParams();

                    if (!parameters.isEmpty()) {
                        Optional.ofNullable(parameters.getFirst(PAGE_INDEX_QUERY_PARAMETER_NAME))
                                .ifPresent(val -> pagingBuilder.pageCount(Integer.parseInt(val) + 1));
                    }
                }
            }

            responseBodyBuilder.paging(pagingBuilder.build());
        }

        @Mapping(source = "singlePaymentInitiationBody.creditorAddress.townName", target = "creditorAddress.city")
        SinglePaymentBody map(PaymentInitiationJson singlePaymentInitiationBody);

        default PagingBuilder getPagingBuilderWithRequestPageData(ServiceContext<ListTransactionsRequest> context) {
            var request = context.getRequest();

            return Paging.builder().page(request.getPage()).pageSize(request.getPageSize());
        }

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
