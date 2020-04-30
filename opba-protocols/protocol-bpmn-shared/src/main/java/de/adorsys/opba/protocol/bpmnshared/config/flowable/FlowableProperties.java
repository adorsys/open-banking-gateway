package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static de.adorsys.opba.protocol.bpmnshared.config.flowable.ConfigConst.FLOWABLE_SHARED_CONFIG_PREFIX;

/**
 * Configures which classes can be serialized/deserialized in JSON form and configures threshold when to use
 * {@link JsonCustomSerializer} or {@link LargeJsonCustomSerializer}
 */
@Data
@Configuration
@ConfigurationProperties(prefix = FLOWABLE_SHARED_CONFIG_PREFIX + "serialization")
public class FlowableProperties {

    private List<String> serializeOnlyPackages;

    @SuppressWarnings("checkstyle:MagicNumber")
    private int maxLength = 2048;

    public boolean canSerialize(String canonicalName) {
        return SerializerUtil.canSerialize(canonicalName, serializeOnlyPackages);
    }
}
