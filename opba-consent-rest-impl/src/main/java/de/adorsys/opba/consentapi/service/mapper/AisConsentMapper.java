package de.adorsys.opba.consentapi.service.mapper;

import de.adorsys.opba.consentapi.model.generated.AisAccountAccessInfo;
import de.adorsys.opba.consentapi.model.generated.PsuAuthRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static de.adorsys.opba.restapi.shared.GlobalConst.CONSENT_MAPPERS_PACKAGE;
import static de.adorsys.opba.restapi.shared.GlobalConst.SPRING_KEYWORD;

@Mapper(componentModel = SPRING_KEYWORD, implementationPackage = CONSENT_MAPPERS_PACKAGE)
public interface AisConsentMapper {

    @Mapping(target = "access", source = "request.consentAuth.consent.access")
    @Mapping(target = "frequencyPerDay", source = "request.consentAuth.consent.frequencyPerDay")
    @Mapping(target = "recurringIndicator", source = "request.consentAuth.consent.recurringIndicator")
    @Mapping(target = "validUntil", source = "request.consentAuth.consent.validUntil")
    @Mapping(target = "combinedServiceIndicator", source = "request.consentAuth.consent.combinedServiceIndicator")
    AisConsent map(PsuAuthRequest request);

    default String map(AisAccountAccessInfo.AllPsd2Enum allPsd2Enum) {
        if (null == allPsd2Enum) {
            return null;
        }

        return allPsd2Enum.toString();
    }

    default String map(AisAccountAccessInfo.AvailableAccountsEnum availableAccountsEnum) {
        if (null == availableAccountsEnum) {
            return null;
        }

        return availableAccountsEnum.toString();
    }
}
