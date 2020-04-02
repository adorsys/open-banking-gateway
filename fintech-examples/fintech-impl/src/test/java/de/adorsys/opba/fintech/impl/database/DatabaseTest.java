package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EnableFinTechImplConfig
@SpringBootTest
@SpringBootApplication
public class DatabaseTest {

    @Autowired
    protected UserRepository userRepository;

    @Test
    public void testSimpleSearch() {
        userRepository.save(createSessionEntity("peter", "1"));
        userRepository.save(createSessionEntity("maksym", "2"));
        userRepository.save(createSessionEntity("valentyn", "3"));

        userRepository.findAll().forEach(en -> log.info(en.toString()));

        assertTrue(userRepository.findById("maksym").isPresent());
        assertFalse(userRepository.findById("maksim").isPresent());
        assertTrue(userRepository.findBySessionCookieValue("1").isPresent());
        assertFalse(userRepository.findBySessionCookieValue("4").isPresent());
    }

    @Test
    public void testDeleteInOneTx() {
        userRepository.save(createSessionEntity("peter", "1"));
        SessionEntity sessionEntity = userRepository.findById("peter").get();
        sessionEntity.setSessionCookieValue(null);
        userRepository.save(sessionEntity);
    }

    @Test
    public void testDeleteInTwoTx() {
        testDeleteInTwoTx1();
        testDeleteInTwoTx2();
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx1() {
        userRepository.save(createSessionEntity("peter", "1"));
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx2() {
        SessionEntity sessionEntity = userRepository.findById("peter").get();
        sessionEntity.setSessionCookieValue(null);
        userRepository.save(sessionEntity);
    }


    private SessionEntity createSessionEntity(String username, String sessionCookieValue) {
        SessionEntity sessionEntity = SessionEntity.builder()
                .loginUserName(username)
                .password("affe")
                .build();
        sessionEntity.setSessionCookieValue(sessionCookieValue);
        sessionEntity.addLogin(OffsetDateTime.now());

        return sessionEntity;
    }
}
