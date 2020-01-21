package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate;

import de.adorsys.opba.core.protocol.domain.entity.BankConfiguration;
import de.adorsys.opba.core.protocol.repository.jpa.BankConfigurationRepository;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aAuthorizationHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.ResponseHeaders.ASPSP_SCA_APPROACH;

@Service("xs2aStartAuthorization")
@RequiredArgsConstructor
public class StartAuthorization extends ValidatedExecution<Xs2aContext> {

    private final BankConfigurationRepository bic;
    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<StartScaProcessResponse> scaStart = ais.startConsentAuthorisation(
                context.getConsentId(),
                Xs2aAuthorizationHeaders.FROM_CTX.map(context).toHeaders()
        );

        context.setAspspScaApproach(scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH));
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        BankConfiguration config = bic.getOne(context.getBankConfigId());

        ContextUtil.getAndUpdateContext(execution, (Xs2aContext ctx) -> {
            ctx.setAspspScaApproach(config.getPreferredApproach().name());
        });
    }
}
