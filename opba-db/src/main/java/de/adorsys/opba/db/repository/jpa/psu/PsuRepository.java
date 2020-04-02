package de.adorsys.opba.db.repository.jpa.psu;

import de.adorsys.opba.db.domain.entity.psu.Psu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PsuRepository extends CrudRepository<Psu, Long> {
}
