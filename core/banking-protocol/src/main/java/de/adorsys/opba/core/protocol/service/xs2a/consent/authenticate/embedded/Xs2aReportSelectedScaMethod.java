package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethodResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Service("xs2aReportSelectedScaMethod")
@RequiredArgsConstructor
public class Xs2aReportSelectedScaMethod extends ValidatedExecution<Xs2aContext> {

    private final Xs2aReportSelectedScaMethod.FromCtx toBody;
    private final Xs2aStandardHeaders.FromCtx toHeaders;
    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    // TODO validation projection
    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<SelectPsuAuthenticationMethodResponse> authResponse = ais.updateConsentsPsuData(
            context.getConsentId(),
            context.getAuthorizationId(),
            toHeaders.map(context).toHeaders(),
            toBody.map(context)
        );

        ContextUtil.getAndUpdateContext(
            execution,
            (Xs2aContext ctx) -> ctx.setScaSelected(authResponse.getBody().getChosenScaMethod())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx {

        @Mapping(target = "authenticationMethodId", source = "userSelectScaId")
        SelectPsuAuthenticationMethod map(Xs2aContext ctx);
    }
}
