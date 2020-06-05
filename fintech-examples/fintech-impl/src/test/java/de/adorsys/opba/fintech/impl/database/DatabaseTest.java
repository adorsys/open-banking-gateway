package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.LoginEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.LoginRepository;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Slf4j
@EnableFinTechImplConfig
@SpringBootTest
@SpringBootApplication
@DirtiesContext(classMode=AFTER_EACH_TEST_METHOD)
public class DatabaseTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SessionRepository sessionRepository;

    @Autowired
    protected LoginRepository loginRepository;

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
    public void testCookieValueIsUnique() {
        createUserEntity("peter", "1");
        createUserEntity("maksym", "1");
        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class, () -> sessionRepository.findBySessionCookieValue("1"));
    }

    @Test
    public void testLogins() {
        assertFalse(userRepository.findById("peter").isPresent());
        assertFalse(userRepository.existsById("peter"));
        createUserEntity("peter", "1");
        UserEntity userEntity = userRepository.findById("peter").get();

        loginRepository.save(new LoginEntity(userEntity));
        loginRepository.save(new LoginEntity(userEntity));
        loginRepository.save(new LoginEntity(userEntity));

        createUserEntity("maksym", "1");
        UserEntity userEntity2 = userRepository.findById("peter").get();

        loginRepository.save(new LoginEntity(userEntity2));
        loginRepository.save(new LoginEntity(userEntity2));
        loginRepository.save(new LoginEntity(userEntity2));

    }

    @Test
    public void testDeleteInOneTx() {
        createUserEntity("peter", "1");
        UserEntity userEntity = userRepository.findById("peter").get();
        sessionRepository.deleteAll(sessionRepository.findByUserEntity(userEntity));
        loginRepository.deleteAll(loginRepository.findByUserEntityOrderByLoginTimeDesc(userEntity));
        userRepository.delete(userEntity);
    }

    @Test
    public void testFindAllSessions() {
        String[] sessionCookieValues = {"1","2","3"};
        createUserEntity("peter", sessionCookieValues);
        UserEntity userEntity = userRepository.findById("peter").get();

        // find from users side
        List<SessionEntity> petersSessions = new ArrayList<>();
        sessionRepository.findByUserEntity(userEntity).forEach(petersSessions::add);
        assertArrayEquals(sessionCookieValues,petersSessions.stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());
    }


    @Test
    public void testDeleteASession() {
        String[] sessionCookieValues = {"1","2","3"};
        createUserEntity("peter", sessionCookieValues);
        UserEntity userEntity = userRepository.findById("peter").get();

        // find from users side
        List<SessionEntity> petersSessions = new ArrayList<>();
        sessionRepository.findByUserEntity(userEntity).forEach(petersSessions::add);
        assertArrayEquals(sessionCookieValues,petersSessions.stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());

        String[] reducedSessionCookieValues = {"1","3"};
        sessionRepository.delete(sessionRepository.findBySessionCookieValue("2").get());
        petersSessions.clear();
        sessionRepository.findByUserEntity(userEntity).forEach(petersSessions::add);
        assertArrayEquals(reducedSessionCookieValues,petersSessions.stream().map(SessionEntity::getSessionCookieValue).collect(Collectors.toList()).toArray());
    }

    private void createUserEntity(String username, String... sessionCookieValues) {
        UserEntity userEntity = UserEntity.builder()
                .loginUserName(username)
                .password("affe")
                .build();

        userRepository.save(userEntity);
        for (String sessionCookieValue:sessionCookieValues) {
            SessionEntity sessionEntity = new SessionEntity(userEntity, 10, null);
            sessionEntity.setSessionCookieValue(sessionCookieValue);
            loginRepository.save(new LoginEntity(userEntity));
            sessionRepository.save(sessionEntity);
        }
    }
}
