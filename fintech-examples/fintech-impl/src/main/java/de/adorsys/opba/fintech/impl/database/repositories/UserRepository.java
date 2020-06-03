package de.adorsys.opba.fintech.impl.database.repositories;


import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
