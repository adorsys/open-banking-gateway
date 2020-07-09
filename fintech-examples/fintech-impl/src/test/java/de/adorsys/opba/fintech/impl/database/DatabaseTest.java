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
import java.util.ArrayList;

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
        userRepository.save(create("peter", "1"));
        userRepository.save(create("maksym", "2"));
        userRepository.save(create("valentyn", "3"));

        userRepository.findAll().forEach(en -> log.info(en.toString()));

        assertTrue(userRepository.findById("maksym").isPresent());
        assertFalse(userRepository.findById("maksim").isPresent());
        assertTrue(userRepository.findByXsrfToken("1").isPresent());
        assertFalse(userRepository.findByXsrfToken("4").isPresent());
    }

    @Test
    public void testDeleteInOneTx() {
        userRepository.save(create("peter", "1"));
        SessionEntity sessionEntity = userRepository.findById("peter").get();
        sessionEntity.setCookies(new ArrayList<>());
        userRepository.save(sessionEntity);
    }

    @Test
    public void testDeleteInTwoTx() {
        testDeleteInTwoTx1();
        testDeleteInTwoTx2();
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx1() {
        userRepository.save(create("peter", "1"));
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx2() {
        SessionEntity sessionEntity = userRepository.findById("peter").get();
        sessionEntity.setCookies(new ArrayList<>());
        userRepository.save(sessionEntity);
    }


    private SessionEntity create(String username, String xsrf) {
        SessionEntity sessionEntity = SessionEntity.builder()
                .loginUserName(username)
                .password("affe")
                .xsrfToken(xsrf)
                .build();
        sessionEntity.addLogin(OffsetDateTime.now());
        sessionEntity.addCookie("cookie1", "value1");
        sessionEntity.addCookie("cookie2", "value2");
        sessionEntity.addCookie("cookie3", "value3");


        return sessionEntity;
    }
}
