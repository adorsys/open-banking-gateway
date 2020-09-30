package de.adorsys.opba.fireflyexporter.repository;

import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountExportJobRepository extends CrudRepository<AccountExportJob, Long> {
}
