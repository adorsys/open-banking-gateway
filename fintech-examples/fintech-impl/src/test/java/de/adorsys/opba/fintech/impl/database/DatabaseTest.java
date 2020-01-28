package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserProfileEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;

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
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setName("affe");
        userProfileEntity.setLastLogin(OffsetDateTime.now());
        UserEntity te = UserEntity.builder()
                .lastLogin(OffsetDateTime.now())
                .password("affe")
                .xsrfToken("1")
                .userProfile(userProfileEntity)
                .cookies(new ArrayList<>())
                .build();
        te.addCookie("cookie1", "value1");
        te.addCookie("cookie2", "value2");
        te.addCookie("cookie3", "value3");
        userRepository.save(te);
        userRepository.findAll().forEach(en -> log.info(en.toString()));
    }
}
