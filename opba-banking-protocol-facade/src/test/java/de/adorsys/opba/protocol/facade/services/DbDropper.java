package de.adorsys.opba.protocol.facade.services;

import liquibase.integration.spring.SpringLiquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public abstract class DbDropper {

    @Autowired
    private SpringLiquibase liquibase;

    @AfterEach
    @SneakyThrows
    void setup() {
        // drop (drop-first: true) and re-create DB
        liquibase.afterPropertiesSet();
    }
}
