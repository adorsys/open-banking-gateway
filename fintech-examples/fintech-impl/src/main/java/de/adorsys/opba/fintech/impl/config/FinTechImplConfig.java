package de.adorsys.opba.fintech.impl.config;

import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepository;
import de.adorsys.opba.fintech.impl.database.repositories.UserRepositoryImpl;
import de.adorsys.opba.tpp.ais.api.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import de.adorsys.opba.tpp.banksearch.api.resource.generated.TppBankSearchApi;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

@Configuration
@EnableFeignClients
@EnableJpaRepositories(
        basePackages = "de.adorsys.opba.fintech.impl.database.repositories",
        repositoryBaseClass = UserRepositoryImpl.class)
@ComponentScan("de.adorsys.opba.fintech.impl")
@EntityScan(basePackageClasses = {UserEntity.class})

public class FinTechImplConfig {

    @FeignClient(url = "${tpp.url}", name = "tppSearch")
    public interface TppBankSearchClient extends TppBankSearchApi {
    }

    @FeignClient(url = "${tpp.url}", name = "listAccounts")
    public interface TppAisClient extends TppBankingApiAccountInformationServiceAisApi {
    }

    @Bean
    UserRepository userRepository(EntityManager entityManager) {
        return new UserRepositoryImpl(entityManager);
    }
}

