package de.adorsys.opba.protocol.xs2a.service.xs2a.ais;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.Xs2aResultCache;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.Xs2aCachedResultAccessor;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithBalanceParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.AccountList;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Calls ASPSP XS2A API to list the accounts using already existing consent.
 * The result with account list is published as {@link ProcessResponse} to the event bus.
 */
@Service("xs2aAccountListing")
@RequiredArgsConstructor
public class Xs2aAccountListingService extends ValidatedExecution<Xs2aAisContext> {

    private final ApplicationEventPublisher eventPublisher;
    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;
    private final Xs2aConsentErrorHandler handler;
    private final Xs2aCachedResultAccessor resultAccessor;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aAisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);

        var consentId = context.getConsentId();
        if (context.isConsentAcquired() && StringUtils.isEmpty(context.getConsentId())) { // ING specific
            context.setConsentId("DUMMY");
        }
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
        context.setConsentId(consentId);
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aAisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        if (resultFromCache(execution, context)) {
            logResolver.log("doRealExecution: execution ({}) - cached result present", execution);
            return;
        }

        ValidatedQueryHeaders<Xs2aWithBalanceParameters, Xs2aWithConsentIdHeaders> params = extractor.forExecution(context);
        handler.tryActionOrHandleConsentErrors(execution, eventPublisher, () -> {

            logResolver.log("getAccountList with parameters: {}", params.getQuery(), params.getHeaders());

            Response<AccountList> accounts = ais.getAccountList(
                params.getHeaders().toHeaders(),
                params.getQuery().toParameters()
            );

            logResolver.log("getAccountList response: {}", accounts);

            Xs2aResultCache result = resultAccessor.resultFromCache(context).orElse(new Xs2aResultCache());
            result.setAccounts(accounts.getBody());
            resultAccessor.resultToCache(context, result, context.getRequestScoped().consentAccess().getFirstByCurrentSession());

            eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), accounts.getBody())
            );
        });
    }

    private boolean resultFromCache(DelegateExecution execution, Xs2aAisContext context) {
        if (null == context.getOnline() || context.getOnline()) {
           return false;
        }

        Optional<Xs2aResultCache> result = resultAccessor.resultFromCache(context);
        if (result.isEmpty() || null == result.get().getAccounts()) {
            return false;
        }

        eventPublisher.publishEvent(
                new ProcessResponse(execution.getRootProcessInstanceId(), execution.getId(), result.get().getAccounts())
        );
        return true;
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<
        Xs2aAisContext,
        Xs2aWithBalanceParameters,
        Xs2aWithConsentIdHeaders> {

        public Extractor(
            DtoMapper<Xs2aContext, Xs2aWithConsentIdHeaders> toHeaders,
            DtoMapper<Xs2aAisContext, Xs2aWithBalanceParameters> toQuery) {
            super(toHeaders, toQuery);
        }
    }
}
