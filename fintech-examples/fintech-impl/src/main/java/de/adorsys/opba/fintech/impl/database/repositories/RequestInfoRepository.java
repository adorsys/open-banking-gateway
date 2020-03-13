package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RequestInfoRepository extends CrudRepository<RequestInfoEntity, Long> {
    Optional<RequestInfoEntity> findByXsrfToken(String xsrfToken);
}
