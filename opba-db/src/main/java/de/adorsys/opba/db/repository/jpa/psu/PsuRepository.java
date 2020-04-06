package de.adorsys.opba.db.repository.jpa.psu;

import de.adorsys.opba.db.domain.entity.psu.Psu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PsuRepository extends CrudRepository<Psu, Long> {
    Optional<Psu> findByUserId(String id);
}
