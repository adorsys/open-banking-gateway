package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
import de.adorsys.opba.fintech.impl.config.UserRegistrationConfig;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.SessionRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizeService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionLogicService sessionLogicService;
    private final UserRegistrationConfig registrationConfig;

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<UserEntity> login(LoginRequest loginRequest) {
        generateUserIfUserDoesNotExistYet(loginRequest);

        // find user by id
        Optional<UserEntity> optionalUserEntity = findUser(loginRequest.getUsername());
        if (!optionalUserEntity.isPresent() || !optionalUserEntity.get().isActive()) {
            // user not found
            return Optional.empty();
        }

        if (!encoder.matches(loginRequest.getPassword(), optionalUserEntity.get().getPassword())) {
            // wrong password
            return Optional.empty();
        }

        log.debug("User {} is going to be logged in", loginRequest.getUsername());
        return optionalUserEntity;
    }


    @Transactional
    public void logout() {
        SessionEntity sessionEntity = sessionLogicService.getSession();
        log.info("logout for user {}", sessionEntity.getUserEntity().getLoginUserName());
        sessionRepository.delete(sessionEntity);
    }

    @Transactional
    public Optional<UserEntity> findUser(String login) {
        return userRepository.findById(login);
    }

    @Transactional
    public UserEntity createUser(String login, String password) {
        return userRepository.save(createUserEntityButDontSave(login, password));
    }

    public UserEntity createUserEntityButDontSave(String username, String password) {
        return UserEntity.builder()
                .loginUserName(username)
                .fintechUserId(createID(username))
                .password(encoder.encode(password))
                .active(true)
                .serviceAccount(false)
                .build();
    }

    public UserEntity updatePasswordButDontSave(UserEntity user, String password) {
        user.setPassword(encoder.encode(password));
        return user;
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (UserRegistrationConfig.SecurityState.ALLOW != registrationConfig.getSimple()) {
            log.debug("Simple user registration disabled");
            return;
        }

        if (userRepository.existsById(loginRequest.getUsername())) {
            log.debug("User {} exists ", loginRequest.getUsername());
            return;
        }
        log.info("create on the fly user {}", loginRequest.getUsername());

        createUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    private String createID(String username) {
        return new String(Hex.encode(username.getBytes()));
    }
}
