package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

import static de.adorsys.opba.protocol.bpmnshared.config.flowable.ConfigConst.FLOWABLE_SHARED_CONFIG_PREFIX;


@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = FLOWABLE_SHARED_CONFIG_PREFIX)
public class FlowableProperties {
    /**
     * Configures number of retries for flowable engine and for retry template config
     */
    @Min(0)
    @SuppressWarnings("checkstyle:MagicNumber")
    private int numberOfRetries = 3;

    @NotNull
    private Serialization serialization;

    @NotNull
    private Expirable expirable;

    @Data
    @Configuration
    public static class Serialization {
        /**
         * Configures which classes can be serialized/deserialized in JSON form and configures threshold when to use
         * {@link JsonCustomSerializer} or {@link LargeJsonCustomSerializer}
         */
        @NotEmpty
        private List<@NotBlank String> serializeOnlyPackages;

        @Min(-1)
        @SuppressWarnings("checkstyle:MagicNumber")
        private int maxLength = 2048;

        public boolean canSerialize(String canonicalName) {
            return SerializerUtil.canSerialize(canonicalName, serializeOnlyPackages);
        }
    }

    @Data
    @Configuration
    public static class Expirable {
        /**
         * Duration for which the record will be alive and it will be removed when this time frame passes.
         */
        @NotNull
        private Duration expireAfterWrite;
    }
}
