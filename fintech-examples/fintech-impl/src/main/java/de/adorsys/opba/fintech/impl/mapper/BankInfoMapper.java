
package de.adorsys.opba.fintech.impl.mapper;

import de.adorsys.opba.fintech.api.model.generated.InlineResponseBankInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationPackage = "de.adorsys.opba.fintech.impl.mapper.generated")
public interface BankInfoMapper {
    InlineResponseBankInfo mapFromTppToFintech(
            de.adorsys.opba.tpp.bankinfo.api.model.generated.BankInfoResponse bankInfoResponse
    );
}
