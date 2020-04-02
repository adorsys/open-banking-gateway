package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedirectUrlRepository extends CrudRepository<RedirectUrlsEntity, Long> {
    Optional<RedirectUrlsEntity> findByRedirectCode(String redirectCode);
}
