package de.adorsys.opba.protocol.xs2a.config.flowable;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures which classes can be serialized/deserialized in JSON form and configures threshold when to use
 * {@link JsonCustomSerializer} or {@link LargeJsonCustomSerializer}
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "opba.xs2a.flowable")
public class Xs2aFlowableProperties {

    private List<String> serializeOnlyPackages = ImmutableList.of(
            "de.adorsys.opba.protocol.xs2a.service.xs2a.context",
            "de.adorsys.xs2a.adapter.service.model"
    );

    @SuppressWarnings("checkstyle:MagicNumber")
    private int maxLength = 2048;

    public boolean canSerialize(String canonicalName) {
        return SerializerUtil.canSerialize(canonicalName, serializeOnlyPackages);
    }
}
