package de.adorsys.opba.protocol.xs2a.service.xs2a.consent;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.repository.jpa.ConsentRepository;
import de.adorsys.opba.protocol.xs2a.BaseMockitoTest;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aFlowableProperties;
import de.adorsys.opba.protocol.xs2a.config.flowable.Xs2aObjectMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class Xs2aLoadConsentAndContextFromDbTest extends BaseMockitoTest {

    @Mock
    private Xs2aLoadConsentAndContextFromDb.ContextMerger merger;

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField") /// Injected into class
    private Xs2aFlowableProperties properties;

    @Mock
    private Xs2aObjectMapper mapper;

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private Xs2aContext context;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private Xs2aLoadConsentAndContextFromDb contextFromDb;

    @Test
    void testNullContextIsNotLoaded() {
        UUID sessionId = UUID.randomUUID();
        when(context.getServiceSessionId()).thenReturn(sessionId);
        when(consentRepository.findByServiceSessionId(sessionId)).thenReturn(Optional.of(Consent.builder().build()));

        contextFromDb.doRealExecution(execution, context);

        verifyNoInteractions(mapper);
        verifyNoInteractions(merger);
    }
}