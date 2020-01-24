package de.adorsys.opba.core.protocol.service;

import de.adorsys.opba.core.protocol.BaseMockitoTest;
import org.flowable.common.engine.api.FlowableOptimisticLockingException;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static de.adorsys.opba.core.protocol.TestProfiles.ONE_TIME_POSTGRES_RAMFS;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(ONE_TIME_POSTGRES_RAMFS)
class ContextUpdateServiceTest extends BaseMockitoTest {

    @MockBean
    private RuntimeService runtimeService;

    @Autowired
    private ContextUpdateService updateService;

    @Test
    void updateContextRetriesOnFlowableOptimisticLockingException() {
        String execId = "FOO-BAR";
        String value = "CTX";

        when(runtimeService.getVariable(execId, CONTEXT)).thenReturn(value);
        doThrow(new FlowableOptimisticLockingException("FAIL")).doAnswer(inv -> null)
                .when(runtimeService)
                .setVariable(execId, CONTEXT, value);

        updateService.updateContext(execId, ctx -> ctx);

        verify(runtimeService, times(2)).getVariable(execId, CONTEXT);
        verify(runtimeService, times(2)).setVariable(execId, CONTEXT, value);
    }
}
