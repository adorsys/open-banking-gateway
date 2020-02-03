package de.adorsys.opba.fintech.impl.service.mapper;

import de.adorsys.opba.fintech.api.model.generated.AccountReport;
import org.mapstruct.Mapper;

@Mapper
public interface AccountReportMapper {
    AccountReport map(de.adorsys.opba.tpp.ais.api.model.generated.AccountReport accountReport);
}
