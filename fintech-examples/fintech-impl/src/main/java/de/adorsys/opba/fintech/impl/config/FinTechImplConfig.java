package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

@Configuration
@EnableFeignClients(basePackages = "de.adorsys.opba.fintech.impl.tppclients")
@EnableJpaRepositories(
        basePackages = "de.adorsys.opba.fintech.impl.database.repositories",
        repositoryBaseClass = UserRepositoryImpl.class)
@ComponentScan("de.adorsys.opba.fintech.impl")
@EntityScan(basePackageClasses = {SessionEntity.class})
@Slf4j
public class FinTechImplConfig {

    @Bean
    UserRepository userRepository(EntityManager entityManager) {
        return new UserRepositoryImpl(entityManager);
    }

}

