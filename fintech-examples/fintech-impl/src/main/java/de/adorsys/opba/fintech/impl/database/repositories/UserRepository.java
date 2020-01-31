package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserRepository
        extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByXsrfToken(String xsrfToken);
}
