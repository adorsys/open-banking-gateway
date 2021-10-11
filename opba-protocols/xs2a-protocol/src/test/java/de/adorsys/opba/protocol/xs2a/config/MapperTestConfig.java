package de.adorsys.opba.protocol.xs2a.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ObjectMapperConfig.class)
@ComponentScan({"de.adorsys.opba.protocol.xs2a.service.mappers.generated",
        "de.adorsys.opba.protocol.xs2a.util",
        "de.adorsys.opba.protocol.xs2a.entrypoint.parsers",
        "de.adorsys.opba.protocol.xs2a.domain.dto.forms"})
public class MapperTestConfig {
}
