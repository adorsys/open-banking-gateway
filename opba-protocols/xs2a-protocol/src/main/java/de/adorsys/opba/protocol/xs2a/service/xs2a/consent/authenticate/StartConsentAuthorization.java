package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate;

import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.ResponseHeaders.ASPSP_SCA_APPROACH;

/**
 * Initiates the consent authorization. Optionally may provide preferred ASPSP approach.
 */
@Service("xs2aStartConsentAuthorization")
@RequiredArgsConstructor
public class StartConsentAuthorization extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        CurrentBankProfile config = context.aspspProfile();

        ValidatedPathHeaders<Xs2aInitialConsentParameters, Xs2aStandardHeaders> params =
                extractor.forExecution(context);
        Response<StartScaProcessResponse> scaStart = ais.startConsentAuthorisation(
                params.getPath().getConsentId(),
                params.getHeaders().toHeaders()
        );

        String aspspSelectedApproach = scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH);
        context.setAspspScaApproach(null == aspspSelectedApproach ? config.getPreferredApproach().name() : aspspSelectedApproach);
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        CurrentBankProfile config = context.aspspProfile();

        ContextUtil.getAndUpdateContext(execution, (Xs2aContext ctx) -> {
            ctx.setAspspScaApproach(config.getPreferredApproach().name());
            ctx.setAuthorizationId(UUID.randomUUID().toString());
        });
    }

    @Service
    public static class Extractor extends PathHeadersMapperTemplate<
                    Xs2aContext,
                    Xs2aInitialConsentParameters,
                    Xs2aStandardHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aInitialConsentParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }
}
