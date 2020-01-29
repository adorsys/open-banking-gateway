package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
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
        UserEntity userEntity = userRepository.findById("peter").get();
        userEntity.setCookies(new ArrayList<>());
        userRepository.save(userEntity);
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
        UserEntity userEntity = userRepository.findById("peter").get();
        userEntity.setCookies(new ArrayList<>());
        userRepository.save(userEntity);
    }


    private UserEntity create(String username, String xsrf) {
        UserEntity userEntity = UserEntity.builder()
                .name(username)
                .password("affe")
                .xsrfToken(xsrf)
                .build();
        userEntity.addLogin(OffsetDateTime.now());
        userEntity.addCookie("cookie1", "value1");
        userEntity.addCookie("cookie2", "value2");
        userEntity.addCookie("cookie3", "value3");


        return userEntity;
    }
}
