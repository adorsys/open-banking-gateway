package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.BaseMockitoTest;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthenticationResponse;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Xs2aAuthenticateUserConsentClearsPasswordTest extends BaseMockitoTest {

    private static final String PASSWORD_VALUE = "12345";

    @Mock
    private Xs2aAuthenticateUserConsent.Extractor extractor;

    @Mock
    private Xs2aValidator validator;

    @Mock
    private AccountInformationService ais;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> mockParams;

    @Mock
    private Response<UpdatePsuAuthenticationResponse> psuResponse;

    @Mock
    private UpdatePsuAuthenticationResponse responseBody;

    @InjectMocks
    private Xs2aAuthenticateUserConsentTestable tested;

    @Test
    void pinCleaned() {
        Xs2aContext context = new Xs2aContext();
        context.setTransientStorage(new TransientDataStorage(new HashMap<>()));
        when(mockParams.getHeaders()).thenReturn(new Xs2aStandardHeaders());
        when(mockParams.getBody()).thenReturn(new UpdatePsuAuthentication());
        when(mockParams.getPath()).thenReturn(new Xs2aAuthorizedConsentParameters());
        when(delegateExecution.getVariable(CONTEXT)).thenReturn(context);
        context.setPsuPassword(PASSWORD_VALUE);
        when(extractor.forExecution(context)).thenReturn(mockParams);
        when(ais.updateConsentsPsuData(any(), any(), any(), any(UpdatePsuAuthentication.class))).thenReturn(psuResponse);
        when(psuResponse.getBody()).thenReturn(responseBody);

        tested.doRealExecution(delegateExecution, context);

        verify(delegateExecution).setVariable(CONTEXT, context);
        assertThat(context.getPsuPassword()).isNull();
    }

    public static class Xs2aAuthenticateUserConsentTestable extends Xs2aAuthenticateUserConsent {
        public Xs2aAuthenticateUserConsentTestable(Extractor extractor, Xs2aValidator validator, AccountInformationService ais) {
            super(extractor, validator, ais);
        }

        @Override
        public void doRealExecution(DelegateExecution execution, Xs2aContext context) {
            super.doRealExecution(execution, context);
        }
    }
}