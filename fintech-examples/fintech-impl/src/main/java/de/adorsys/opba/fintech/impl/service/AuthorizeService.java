package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.LoginRequest;
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

    /**
     * @param loginRequest
     * @return empty, if user not found or password not valid. otherwise optional of userprofile
     */
    @Transactional
    public Optional<UserEntity> login(LoginRequest loginRequest) {
        generateUserIfUserDoesNotExistYet(loginRequest);

        // find user by id
        Optional<UserEntity> optionalUserEntity = userRepository.findById(loginRequest.getUsername());
        if (!optionalUserEntity.isPresent()) {
            // user not found
            return Optional.empty();
        }

        if (!encoder.matches(loginRequest.getPassword(), optionalUserEntity.get().getPassword())) {
            // wrong password
            return Optional.empty();
        }

        return optionalUserEntity;
    }


    @Transactional
    public void logout() {
        SessionEntity sessionEntity = sessionLogicService.getSession();
        log.info("logout for user {}", sessionEntity.getUserEntity().getLoginUserName());
        sessionRepository.delete(sessionEntity);
    }

    private void generateUserIfUserDoesNotExistYet(LoginRequest loginRequest) {
        if (userRepository.existsById(loginRequest.getUsername())) {
            log.debug("User {} exists ", loginRequest.getUsername());
            return;
        }
        log.info("create on the fly user {}", loginRequest.getUsername());

        userRepository.save(
                UserEntity.builder()
                        .loginUserName(loginRequest.getUsername())
                        .fintechUserId(createID(loginRequest.getUsername()))
                        .password(encoder.encode(loginRequest.getPassword()))
                        .build());
    }

    private String createID(String username) {
        return new String(Hex.encode(username.getBytes()));
    }

}
