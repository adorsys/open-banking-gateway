package de.adorsys.opba.fintech.impl.database.repositories;


import de.adorsys.opba.fintech.impl.database.entities.OauthSessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface OauthSessionEntityRepository extends CrudRepository<OauthSessionEntity, String> {
}
