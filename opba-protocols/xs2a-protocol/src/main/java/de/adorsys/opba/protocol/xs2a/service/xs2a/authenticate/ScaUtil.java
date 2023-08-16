package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.xs2a.adapter.api.ResponseHeaders;
import de.adorsys.xs2a.adapter.api.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import de.adorsys.xs2a.adapter.api.model.SelectPsuAuthenticationMethodResponse;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthenticationResponse;
import lombok.experimental.UtilityClass;

import static de.adorsys.xs2a.adapter.api.model.AspspScaApproach.DECOUPLED;

@UtilityClass
public class ScaUtil {

    public AuthenticationObject scaMethodSelected(UpdatePsuAuthenticationResponse response) {
        var chosenScaMethod = response.getChosenScaMethod();

        if (null != chosenScaMethod) {
            return mapToAuthenticationObject(chosenScaMethod);
        }

        if (ScaStatus.SCAMETHODSELECTED == response.getScaStatus()) {
            var result = new AuthenticationObject();
            result.setExplanation(response.getPsuMessage());
            return result;
        }

        return null;
    }

    public AuthenticationObject scaMethodSelected(SelectPsuAuthenticationMethodResponse response) {
        var chosenScaMethod = response.getChosenScaMethod();

        if (null != chosenScaMethod) {
            return mapToAuthenticationObject(chosenScaMethod);
        }

        if (ScaStatus.SCAMETHODSELECTED == response.getScaStatus()) {
            var result = new AuthenticationObject();
            result.setExplanation(response.getPsuMessage());
            return result;
        }

        return null;
    }

    public AuthenticationObject scaMethodSelected(StartScaprocessResponse response) {
        var chosenScaMethod = response.getChosenScaMethod();

        if (null != chosenScaMethod) {
            return mapToAuthenticationObject(chosenScaMethod);
        }

        if (ScaStatus.SCAMETHODSELECTED == response.getScaStatus()) {
            var result = new AuthenticationObject();
            result.setExplanation(response.getPsuMessage());
            return result;
        }

        return null;
    }

    public boolean isDecoupled(ResponseHeaders headers) {
        if (null == headers) {
            return false;
        }

        return DECOUPLED.name().equals(headers.getHeader(ResponseHeaders.ASPSP_SCA_APPROACH));
    }

    private AuthenticationObject mapToAuthenticationObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(object, AuthenticationObject.class);
    }
}
