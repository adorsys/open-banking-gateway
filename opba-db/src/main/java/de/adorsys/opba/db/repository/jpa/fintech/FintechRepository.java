package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FintechRepository extends CrudRepository<Fintech, Long> {

    Optional<Fintech> findByGlobalId(String globalId);
}
