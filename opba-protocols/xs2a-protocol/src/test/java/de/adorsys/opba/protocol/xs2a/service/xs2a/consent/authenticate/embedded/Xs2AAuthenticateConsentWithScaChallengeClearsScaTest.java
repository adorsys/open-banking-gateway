package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.BaseMockitoTest;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.consent.RequestScopedStub;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatus;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Xs2AAuthenticateConsentWithScaChallengeClearsScaTest extends BaseMockitoTest {

    private static final String SCA_VALUE = "123456";

    @Mock
    private Xs2aAuthenticateConsentWithScaChallenge.Extractor extractor;

    @Mock
    private AccountInformationService ais;

    @Mock
    private AuthorizationErrorSink errorSink;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, TransactionAuthorisation> mockParams;

    @Mock
    private Response<ScaStatusResponse> scaStatus;

    @Mock
    private ScaStatusResponse responseBody;

    @InjectMocks
    private Xs2AAuthenticateConsentWithScaChallengeTestable tested;

    @Test
    void scaChallengeCleaned() {
        Xs2aContext context = new Xs2aContext();
        context.setRequestScoped(new RequestScopedStub());
        doCallRealMethod().when(errorSink).swallowAuthorizationErrorForLooping(any(), any());
        when(mockParams.getHeaders()).thenReturn(new Xs2aStandardHeaders());
        when(mockParams.getBody()).thenReturn(new TransactionAuthorisation());
        when(mockParams.getPath()).thenReturn(new Xs2aAuthorizedConsentParameters());
        when(delegateExecution.getVariable(CONTEXT)).thenReturn(context);
        context.setPsuPassword(SCA_VALUE);
        when(extractor.forExecution(context)).thenReturn(mockParams);
        when(ais.updateConsentsPsuData(any(), any(), any(), any(TransactionAuthorisation.class))).thenReturn(scaStatus);
        when(scaStatus.getBody()).thenReturn(responseBody);
        when(scaStatus.getBody().getScaStatus()).thenReturn(ScaStatus.EXEMPTED);

        tested.doRealExecution(delegateExecution, context);

        verify(delegateExecution).setVariable(CONTEXT, context);
        assertThat(context.getPsuPassword()).isNull();
    }

    public static class Xs2AAuthenticateConsentWithScaChallengeTestable extends Xs2aAuthenticateConsentWithScaChallenge {
        public Xs2AAuthenticateConsentWithScaChallengeTestable(Extractor extractor, Xs2aValidator validator, AccountInformationService ais, AuthorizationErrorSink errorSink) {
            super(extractor, validator, ais, errorSink);
        }

        @Override
        public void doRealExecution(DelegateExecution execution, Xs2aContext context) {
            super.doRealExecution(execution, context);
        }
    }
}