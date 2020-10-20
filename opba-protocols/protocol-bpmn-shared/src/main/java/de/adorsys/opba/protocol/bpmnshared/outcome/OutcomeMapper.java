package de.adorsys.opba.protocol.bpmnshared.outcome;

import de.adorsys.opba.protocol.bpmnshared.dto.messages.ConsentAcquired;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.Redirect;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.InternalReturnableProcessError;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ValidationProblem;

/**
 * Mapper to convert from internal protocol result to facade facing protocol result.
 *
 * @param <T>
 */
public interface OutcomeMapper<T> {

    void onSuccess(ProcessResponse responseResult);

    void onRedirect(Redirect redirectResult);

    void onValidationProblem(ValidationProblem problem);

    void onConsentAcquired(ConsentAcquired acquired);

    void onReturnableProcessError(InternalReturnableProcessError internalReturnableProcessError);

    void onError();
}
