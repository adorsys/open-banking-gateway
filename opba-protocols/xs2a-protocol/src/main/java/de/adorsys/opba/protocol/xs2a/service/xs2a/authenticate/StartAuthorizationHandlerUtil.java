package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.model.HrefType;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

@UtilityClass
public class StartAuthorizationHandlerUtil {
    private static final String START_AUTHORISATION = "startAuthorisation";
    private static final String START_AUTHORISATION_WITH_PSU_IDENTIFICATION = "startAuthorisationWithPsuIdentification";
    private static final String START_AUTHORISATION_WITH_PROPRIETARY_DATA = "startAuthorisationWithProprietaryData";
    private static final String START_AUTHORISATION_WITH_PSU_AUTHENTICATION = "startAuthorisationWithPsuAuthentication";
    private static final String START_AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION = "startAuthorisationWithEncryptedPsuAuthentication";
    private static final String AUTHORISATION = "authorisations/";
    private static final List<String> START_AUTHORIZATION_KEYS = List.of(START_AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION, START_AUTHORISATION,
            START_AUTHORISATION_WITH_PSU_AUTHENTICATION, START_AUTHORISATION_WITH_PROPRIETARY_DATA, START_AUTHORISATION_WITH_PSU_IDENTIFICATION);

    public void handleImplicitAuthorizationStartIfPossible(Map<String, HrefType> bodyLinks, Xs2aContext context) {
        if (null == bodyLinks) {
            return;
        }
        bodyLinks.keySet().stream()
                .filter(START_AUTHORIZATION_KEYS::contains).findAny()
                .ifPresent(key -> {
                    String path = URI.create(bodyLinks.get(key).getHref()).getPath();
                    String authorizationId = StringUtils.substringAfterLast(path, AUTHORISATION);
                    if (!Strings.isNullOrEmpty(authorizationId)) {
                        context.setAuthorizationId(authorizationId);
                        context.setPasswordShouldBeEncrypted(START_AUTHORISATION_WITH_ENCRYPTED_PSU_AUTHENTICATION.equals(key));
                    }
                });
    }
}
