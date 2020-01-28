package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.impl.service.entities.TempEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<TempEntity, Long> {
}