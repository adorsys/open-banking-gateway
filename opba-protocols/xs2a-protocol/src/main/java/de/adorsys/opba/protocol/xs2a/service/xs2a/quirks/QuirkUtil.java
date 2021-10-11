package de.adorsys.opba.protocol.xs2a.service.xs2a.quirks;

import com.google.common.base.Strings;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static de.adorsys.xs2a.adapter.api.RequestHeaders.X_GTW_BANK_CODE;

/**
 * Utility to contain hacks and quirks.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") //Checkstyle doesn't recognise Lombok
public class QuirkUtil {

    // TODO: Needed because of https://github.com/adorsys/xs2a/issues/73 and because we can't set "X-OAUTH-PREFERRED" header directly
    public static RequestHeaders pushBicToXs2aAdapterHeaders(Xs2aContext context, RequestHeaders toEnhance) {
        // TODO: Warning, for Adorsys Sandbox for Oauth2-Integrated the adapter should be configured to send proper header
        // due to https://github.com/adorsys/xs2a/issues/73
        String bankCode = context.getRequestScoped().aspspProfile().getBankCode();
        if (!Strings.isNullOrEmpty(bankCode)) {
            Map<String, String> headers = toEnhance.toMap();
            headers.put(X_GTW_BANK_CODE, bankCode);
            return RequestHeaders.fromMap(headers);
        }

        return toEnhance;
    }
}
