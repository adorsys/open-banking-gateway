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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        createUserEntity("peter", "1");
        createUserEntity("maksym", "2");
        createUserEntity("valentyn", "3");

        userRepository.findAll().forEach(en -> log.info(en.toString()));

        assertTrue(userRepository.findById("maksym").isPresent());
        assertFalse(userRepository.findById("maksim").isPresent());
        assertTrue(sessionRepository.findBySessionCookieValue("1").isPresent());
        assertFalse(sessionRepository.findBySessionCookieValue("4").isPresent());
        assertTrue(sessionRepository.findBySessionCookieValue("1").get().getUserEntity().getLoginUserName().equals("peter"));

    }

    @Test
    public void testDeleteInOneTx() {
        createUserEntity("peter", "1");
        UserEntity userEntity = userRepository.findById("peter").get();
        userRepository.delete(userEntity);
    }

    @Test
    public void testFindAllSessions() {
        String[] sessionCookieValues = {"1","2","3"};
        createUserEntity("peter", sessionCookieValues);

        // find from users side
        UserEntity userEntity = userRepository.findById("peter").get();
        assertArrayEquals(sessionCookieValues,userEntity.getSessions().stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());

        List<SessionEntity> sessions = new ArrayList<>();
        sessionRepository.findAll().forEach(sessions::add);
        assertEquals(sessionCookieValues.length, sessions.size());
        sessions.stream().forEach(session -> assertTrue(session.getUserEntity().getLoginUserName().equals(userEntity.getLoginUserName())));
    }


    @Test
    public void testDeleteASession() {
        String[] sessionCookieValues = {"1","2","3"};
        createUserEntity("peter", sessionCookieValues);

        // find from users side
        UserEntity userEntity = userRepository.findById("peter").get();
        assertArrayEquals(sessionCookieValues,userEntity.getSessions().stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());

        String[] reducedSessionCookieValues = {"1","3"};
        sessionRepository.delete(sessionRepository.findBySessionCookieValue("2").get());
//        Assertions.assertArrayEquals(reducedSessionCookieValues,userEntity.getSessions().stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());
    }

    private void createUserEntity(String username, String... sessionCookieValues) {
        UserEntity userEntity = UserEntity.builder()
                .loginUserName(username)
                .password("affe")
                .sessions(new ArrayList<>())
                .build();

        for (String sessionCookieValue:sessionCookieValues) {
            SessionEntity sessionEntity = new SessionEntity(userEntity, 10);
            sessionEntity.setSessionCookieValue(sessionCookieValue);
            userEntity.getSessions().add(sessionEntity);
            userEntity.addLogin(OffsetDateTime.now());
            userRepository.save(userEntity);
            sessionRepository.save(sessionEntity);
        }
    }
}
