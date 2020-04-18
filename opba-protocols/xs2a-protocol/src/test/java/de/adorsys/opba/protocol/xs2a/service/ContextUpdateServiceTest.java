package de.adorsys.opba.protocol.xs2a.service;

import de.adorsys.opba.protocol.xs2a.BaseMockitoTest;
import de.adorsys.opba.protocol.xs2a.EnableXs2aProtocol;
import de.adorsys.opba.protocol.xs2a.TestProfiles;
import de.adorsys.opba.protocol.xs2a.constant.GlobalConst;
import de.adorsys.xs2a.adapter.service.AspspReadOnlyRepository;
import org.flowable.common.engine.api.FlowableOptimisticLockingException;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ContextUpdateServiceTest.TestConfig.class)
@ActiveProfiles(TestProfiles.ONE_TIME_POSTGRES_RAMFS)
class ContextUpdateServiceTest extends BaseMockitoTest {

    @MockBean
    private RuntimeService runtimeService;

    @Autowired
    private ContextUpdateService updateService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField") // Injecting into Spring context
    private AspspReadOnlyRepository aspspReadOnlyRepository;

    @Test
    void updateContextRetriesOnFlowableOptimisticLockingException() {
        String execId = "FOO-BAR";
        String value = "CTXF";

        when(runtimeService.getVariable(execId, GlobalConst.CONTEXT)).thenReturn(value);
        doThrow(new FlowableOptimisticLockingException("FAIL")).doAnswer(inv -> null)
                .when(runtimeService)
                .setVariable(execId, GlobalConst.CONTEXT, value);

        updateService.updateContext(execId, ctx -> ctx);

        verify(runtimeService, times(2)).getVariable(execId, GlobalConst.CONTEXT);
        verify(runtimeService, times(2)).setVariable(execId, GlobalConst.CONTEXT, value);
    }

    @EnableXs2aProtocol
    @SpringBootApplication
    public static class TestConfig {
    }
}
