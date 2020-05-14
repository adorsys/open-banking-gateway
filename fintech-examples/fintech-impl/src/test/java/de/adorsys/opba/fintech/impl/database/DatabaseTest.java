package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
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

    @Autowired
    protected SessionRepository sessionRepository;
    @Test
    public void testSimpleSearch() {
        userRepository.save(createUserEntity("peter", "1"));
        userRepository.save(createUserEntity("maksym", "2"));
        userRepository.save(createUserEntity("valentyn", "3"));

        userRepository.findAll().forEach(en -> log.info(en.toString()));

        assertTrue(userRepository.findById("maksym").isPresent());
        assertFalse(userRepository.findById("maksim").isPresent());
        assertTrue(sessionRepository.findBySessionCookieValue("1").isPresent());
        assertFalse(sessionRepository.findBySessionCookieValue("4").isPresent());
    }

    @Test
    public void testDeleteInOneTx() {
        userRepository.save(createUserEntity("peter", "1"));
        UserEntity userEntity = userRepository.findById("peter").get();
        userRepository.delete(userEntity);
    }

    @Test
    public void testDeleteInTwoTx() {
        testDeleteInTwoTx1();
        testDeleteInTwoTx2();
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx1() {
        userRepository.save(createUserEntity("peter", "1"));
    }

    @Transactional(propagation = Propagation.NEVER)
    void testDeleteInTwoTx2() {
        UserEntity sessionEntity = userRepository.findById("peter").get();
        userRepository.save(sessionEntity);
    }


    private UserEntity createUserEntity(String username, String sessionCookieValue) {
        UserEntity userEntity = UserEntity.builder()
                .loginUserName(username)
                .password("affe")
                .build();

        SessionEntity sessionEntity = new SessionEntity(10);
        userEntity.getSessions().add(sessionEntity);
        sessionRepository.save(sessionEntity);

        userEntity.addLogin(OffsetDateTime.now());


        return userEntity;
    }
}
