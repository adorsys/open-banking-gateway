package de.adorsys.opba.protocol.api.ais;

import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.request.accounts.UpdateExternalAisSessionRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateExternalAisSessionBody;

/**
 * Force 3rd party service to read (re-import) data from ASPSP(bank), may require PSU authorization.
 */
public interface UpdateExternalAisSession extends Action<UpdateExternalAisSessionRequest, UpdateExternalAisSessionBody> {
}
