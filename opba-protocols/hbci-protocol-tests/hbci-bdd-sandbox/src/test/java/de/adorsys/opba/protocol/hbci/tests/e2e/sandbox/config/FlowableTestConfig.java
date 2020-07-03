package de.adorsys.opba.protocol.hbci.tests.e2e.sandbox.config;

import com.google.common.collect.ImmutableList;
import de.adorsys.opba.protocol.api.services.scoped.RequestScopedServicesProvider;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableProperties;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.JsonCustomSerializer;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.LargeJsonCustomSerializer;
import de.adorsys.opba.protocol.sandbox.hbci.config.HbciJsonCustomSerializer;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to share and join two worlds of Flowable (as we don't have OSGi here so either can be picked by configuration scan)
 * - Xs2a/HBCI Production Flowable and Hbci-Sandbox Flowable. However as these are tests, some hacks are acceptable.
 *
 * TODO: Improve code isolation so such hack is not used
 */
@Configuration
public class FlowableTestConfig {

    /**
     * Configures different Json serializers for Production-Flowable and HBCI-Sandbox Flowable. Serializer to be used
     * is determined by variable type.
     */
    @Bean
    @Primary // Select this one instead of FlowableConfig and HbciSandboxFlowableConfig
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> hbciTestCustomizeListenerAndJsonSerializer(
            RequestScopedServicesProvider scopedServicesProvider,
            FlowableProperties flowableProperties,
            FlowableObjectMapper mapper
    ) {
        int maxLength = flowableProperties.getSerialization().getMaxLength();
        List<String> serializeOnlyPackages = flowableProperties.getSerialization().getSerializeOnlyPackages();

        return processConfiguration -> {
            processConfiguration.setCustomPreVariableTypes(
                    new ArrayList<>(
                            ImmutableList.of(
                                    new HbciJsonCustomSerializer(mapper.getMapper()), // HBCI sandbox context is more specific.
                                    new JsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), serializeOnlyPackages, maxLength),
                                    new LargeJsonCustomSerializer(scopedServicesProvider, mapper.getMapper(), serializeOnlyPackages, maxLength)
                            )
                    )
            );
            processConfiguration.setEnableEventDispatcher(true);
            processConfiguration.setAsyncExecutorNumberOfRetries(flowableProperties.getNumberOfRetries());
        };
    }
}
