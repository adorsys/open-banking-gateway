package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.LoginEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface LoginRepository extends CrudRepository<LoginEntity, Long> {
    Iterable<LoginEntity> findByUserEntityOrderByLoginTimeDesc(UserEntity userEntity);
}
