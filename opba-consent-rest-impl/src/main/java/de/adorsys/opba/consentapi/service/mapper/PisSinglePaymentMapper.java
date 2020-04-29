package de.adorsys.opba.consentapi.service.mapper;

import de.adorsys.opba.consentapi.model.generated.SinglePayment;
import de.adorsys.opba.protocol.api.dto.request.authorization.SinglePaymentBody;
import org.mapstruct.Mapper;

import static de.adorsys.opba.restapi.shared.GlobalConst.CONSENT_MAPPERS_PACKAGE;
import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = CONSENT_MAPPERS_PACKAGE)
public interface PisSinglePaymentMapper {

    SinglePaymentBody map(SinglePayment request);
}
