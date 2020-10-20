package de.adorsys.opba.protocol.hbci.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraAuthRequestParam;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ScaMethod;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.hbci.context.HbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import de.adorsys.opba.protocol.hbci.entrypoint.helpers.HbciAuthorizationContinuationService;
import de.adorsys.opba.protocol.hbci.entrypoint.helpers.HbciContextUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.PSU_PASSWORD;
import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.SCA_CHALLENGE_DATA;
import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.SCA_CHALLENGE_ID;

/**
 * Entry point to update context with the input from user and continue process.
 */
@Service("hbciUpdateAuthorization")
@RequiredArgsConstructor
public class HbciUpdateAuthorization implements UpdateAuthorization {

    private final HbciExtendWithServiceContext extender;
    private final HbciAuthorizationContinuationService continuationService;
    private final HbciContextUpdateService ctxUpdater;

    @Override
    public CompletableFuture<Result<UpdateAuthBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();
        ctxUpdater.updateContext(
                executionId,
                (HbciContext toUpdate) -> {
                    updateWithExtras(toUpdate, serviceContext.getRequest().getExtras());
                    updateWithScaChallenges(toUpdate, serviceContext.getRequest().getScaAuthenticationData());
                    toUpdate = extender.extend(toUpdate, serviceContext);
                    return toUpdate;
                }
        );

        return continuationService.handleAuthorizationProcessContinuation(executionId);
    }

    private void updateWithExtras(HbciContext context, Map<ExtraAuthRequestParam, Object> extras) {
        if (null == extras) {
            return;
        }

        if (extras.containsKey(ExtraAuthRequestParam.PSU_ID)) {
            context.setPsuId((String) extras.get(ExtraAuthRequestParam.PSU_ID));
        }
    }

    private void updateWithScaChallenges(HbciContext context, Map<String, String> scaChallenges) {
        if (null == scaChallenges) {
            return;
        }

        if (null != scaChallenges.get(PSU_PASSWORD)) {
            context.setPsuPin(scaChallenges.get(PSU_PASSWORD));
        }

        if (null != scaChallenges.get(SCA_CHALLENGE_DATA)) {
            context.setPsuTan(scaChallenges.get(SCA_CHALLENGE_DATA));
        }

        if (null != scaChallenges.get(SCA_CHALLENGE_ID)) {
            context.setUserSelectScaId(scaChallenges.get(SCA_CHALLENGE_ID));
            context.setUserSelectScaType(context.getAvailableSca().stream()
                    .filter(it -> context.getUserSelectScaId().equals(it.getKey()))
                    .map(ScaMethod::getType)
                    .findFirst().orElse(null));
        }
    }
}


