package de.adorsys.opba.protocol.hbci.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.opba.protocol.bpmnshared.EnableSharedFlowableBpmn;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.JacksonMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSharedFlowableBpmn
public class HbciFlowableConfig {

    @Bean
    JacksonMixin<Credentials, CredentialsMixin> credentialsMixin() {
        return new JacksonMixin<>(Credentials.class, CredentialsMixin.class);
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter"}) // Ctor' arguments are used for Jackson magic
    public abstract static class CredentialsMixin {

        CredentialsMixin(
                @JsonProperty("customerId") String customerId,
                @JsonProperty("userId") String userId,
                @JsonProperty("pin") String pin,
                @JsonProperty("pin2") String pin2
        ) { }
    }
}
