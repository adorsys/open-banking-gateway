package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.hbci.config.HbciScaConfiguration;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import de.adorsys.opba.protocol.hbci.entrypoint.helpers.HbciAuthorizationContinuationService;
import de.adorsys.opba.protocol.hbci.entrypoint.helpers.HbciContextUpdateService;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.SCA_CHALLENGE_ID;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("PMD.UnusedPrivateField")
@SpringBootTest( classes = {HbciScaConfiguration.class, HbciUpdateAuthorization.class, HbciContextUpdateService.class})
@EnableConfigurationProperties
@ActiveProfiles(profiles = "test")
class HbciUpdateAuthorizationTest {

    @MockBean
    private RuntimeService runtimeService;
    @MockBean
    private HbciExtendWithServiceContext extender;
    @MockBean
    private HbciAuthorizationContinuationService continuationService;
    @Autowired
    private HbciContextUpdateService ctxUpdater;
    @Autowired
    HbciUpdateAuthorization hbciUpdateAuthorization;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.initMocks(HbciUpdateAuthorizationTest.class);
    }

    @Test
    void AuthorizationTypeMatcherTest() {
        HbciContext hbciContext = new HbciContext();
        hbciContext.setAvailableSca(List.of(
            new ScaMethod("901", "Mobile-TAN", "Mobile-TAN"),
            new ScaMethod("902", "photoTAN-Verfahren", "photoTAN-Verfahren")
        ));

        String authContext = "265e0462-a440-11eb-8224-acde48001122";
        Mockito.when(runtimeService.getVariable(authContext, CONTEXT)).thenReturn(hbciContext);

        ServiceContext<AuthorizationRequest> serviceContext = ServiceContext.<AuthorizationRequest>builder()
            .ctx(Context.<AuthorizationRequest>builder()
                     .authContext(authContext)
                     .serviceSessionId(UUID.randomUUID())
                     .request(AuthorizationRequest.builder().scaAuthenticationData(Collections.singletonMap(SCA_CHALLENGE_ID, "902")).build())
                     .build())
            .build();
        hbciUpdateAuthorization.execute(serviceContext);

        assertThat(hbciContext.getSelectedScaType()).isEqualTo("PHOTO_OTP");
    }
}