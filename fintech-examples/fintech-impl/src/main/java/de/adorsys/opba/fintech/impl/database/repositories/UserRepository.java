package de.adorsys.opba.fintech.impl.database.repositories;


import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    @Modifying
    @Query("UPDATE UserEntity u SET u.active = false WHERE u.serviceAccount = true")
    int deactivateAllServiceAccounts();
}
