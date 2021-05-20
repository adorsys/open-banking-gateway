package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.context.ContextMode;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathQueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ExternalValidationModeDeclaration;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ValidationMode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aResourceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aTransactionParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.TransactionsResponse200Json;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

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
    private final Xs2aConsentErrorHandler handler;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doValidate(DelegateExecution execution, TransactionListXs2aContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);

        var consentId = context.getConsentId();
        if (context.isConsentAcquired() && StringUtils.isEmpty(context.getConsentId())) { // ING specific
            context.setConsentId("DUMMY");
        }
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
        context.setConsentId(consentId);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, TransactionListXs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ValidatedPathQueryHeaders<Xs2aResourceParameters, Xs2aTransactionParameters, Xs2aWithConsentIdHeaders> params =
            extractor.forExecution(context);

        handler.tryActionOrHandleConsentErrors(execution, eventPublisher, () -> {

            logResolver.log("getTransactionList with parameters: {}", params.getPath(), params.getQuery(), params.getHeaders());

            Response<TransactionsResponse200Json> transactionList = ais.getTransactionList(
                params.getPath().getResourceId(),
                params.getHeaders().toHeaders(),
                params.getQuery().toParameters()
            );

            logResolver.log("getTransactionList response: {}", transactionList);

            eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), transactionList.getBody())
            );
        });

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

    /**
     * Special override for ListTransaction resourceId validation for the case when FinTech requires global consent
     * for transactions, acccounts, balances on all accounts. Should not be used in real ListTransactions call,
     * only in mocked ones.
     */
    @Service
    public static class ResourceIdOptionalIfListTransactionsForConsent implements ExternalValidationModeDeclaration {
        @Override
        public Set<FieldCode> appliesTo() {
            return Collections.singleton(FieldCode.RESOURCE_ID);
        }

        @Override
        public boolean appliesToContext(Xs2aContext context) {
            return ProtocolAction.LIST_TRANSACTIONS.equals(context.getAction())
                && context.getServiceSessionId() != null
                && context.getMode() == ContextMode.MOCK_REAL_CALLS;
        }

        @Override
        public ValidationMode computeValidationMode(Xs2aContext context) {
            return ValidationMode.OPTIONAL;
        }
    }
}
