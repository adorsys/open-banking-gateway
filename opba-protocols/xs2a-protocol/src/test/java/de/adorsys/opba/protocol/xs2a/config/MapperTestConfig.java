package de.adorsys.opba.protocol.xs2a.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"de.adorsys.opba.protocol.xs2a.service.mappers.generated",
        "de.adorsys.opba.protocol.xs2a.util",
        "de.adorsys.opba.protocol.xs2a.domain.dto.forms"})
public class MapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                       .registerModule(new JavaTimeModule())
                       .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
