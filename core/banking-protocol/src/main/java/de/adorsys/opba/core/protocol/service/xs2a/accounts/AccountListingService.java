package de.adorsys.opba.core.protocol.service.xs2a.accounts;

import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.core.protocol.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aWithBalanceParameters;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aWithConsentIdHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.AccountListHolder;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aAccountListing")
@RequiredArgsConstructor
public class AccountListingService extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ValidatedQueryHeaders<Xs2aWithBalanceParameters, Xs2aWithConsentIdHeaders> params = extractor.forExecution(context);
        Response<AccountListHolder> accounts = ais.getAccountList(
                params.getHeaders().toHeaders(),
                params.getQuery().toParameters()
        );

        ContextUtil.setResult(execution, accounts.getBody());
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        ContextUtil.setResult(execution, new AccountListHolder());
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<
                    Xs2aContext,
                    Xs2aWithBalanceParameters,
                    Xs2aWithConsentIdHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aWithConsentIdHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aWithBalanceParameters> toQuery) {
            super(toHeaders, toQuery);
        }
    }
}
