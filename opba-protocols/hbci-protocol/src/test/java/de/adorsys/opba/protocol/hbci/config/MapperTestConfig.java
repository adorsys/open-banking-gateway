package de.adorsys.opba.protocol.hbci.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ObjectMapperConfig.class)
@ComponentScan({"de.adorsys.opba.protocol.hbci.service.mappers.generated",
        "de.adorsys.opba.protocol.hbci.util"})
public class MapperTestConfig {
}
