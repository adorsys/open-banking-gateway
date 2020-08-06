package de.adorsys.opba.protocol.hbci.service.consent;

import de.adorsys.multibanking.domain.response.AbstractResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HbciScaRequiredUtil {

    public boolean extraCheckIfScaRequired(AbstractResponse response) {
        // Feature flag to disable:
        if ("false".equals(System.getProperty("RESPONSE_SCA_CHECK"))) {
            return false;
        }

        return response.containsMessage("0030");
    }
}
