package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.TempEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@EnableFinTechImplConfig
@SpringBootTest
@SpringBootApplication
public class DatabaseTest {
    @Autowired
    protected UserRepository userRepository;

    @Test
    public void testDatabase() {
        doTransaction();
    }

    @Transactional
    public void doTransaction() {
        TempEntity te = TempEntity.builder()
                .lastLogin(OffsetDateTime.now())
                .password("affe")
                .xsrfToken("1")
                .build();
        userRepository.save(te);
        userRepository.findAll().forEach(en -> log.info(en.toString()));
    }
}
