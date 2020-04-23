package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathQueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aTransactionParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Calls ASPSP XS2A API to list transactions of the account using already existing consent.
 * The result with account list is published as {@link ProcessResponse} to the event bus.
 */
@Service("xs2aTransactionListing")
@RequiredArgsConstructor
public class Xs2aTransactionListingService extends ValidatedExecution<TransactionListXs2aContext> {

    private final ApplicationEventPublisher eventPublisher;
    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;

    @Override
    protected void doValidate(DelegateExecution execution, TransactionListXs2aContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber") // Hardcoded as it is POC, these should be read from context
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        ValidatedPathQueryHeaders<Xs2aResourceParameters, Xs2aTransactionParameters, Xs2aWithConsentIdHeaders> params =
                extractor.forExecution(context);

        Response<TransactionsReport> transactionList = ais.getTransactionList(
                params.getPath().getResourceId(),
                params.getHeaders().toHeaders(),
                params.getQuery().toParameters()
        );

        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), transactionList.getBody())
        );
    }

    @Service
    public static class Extractor extends PathQueryHeadersMapperTemplate<
                TransactionListXs2aContext,
                Xs2aResourceParameters,
                Xs2aTransactionParameters,
                Xs2aWithConsentIdHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aWithConsentIdHeaders> toHeaders,
                DtoMapper<TransactionListXs2aContext, Xs2aResourceParameters> toPath,
                DtoMapper<TransactionListXs2aContext, Xs2aTransactionParameters> toQuery) {
            super(toHeaders, toPath, toQuery);
        }
    }
}
