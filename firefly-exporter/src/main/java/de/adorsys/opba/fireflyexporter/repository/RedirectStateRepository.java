package de.adorsys.opba.fireflyexporter.repository;

import de.adorsys.opba.fireflyexporter.entity.RedirectState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RedirectStateRepository extends CrudRepository<RedirectState, UUID> {
}
