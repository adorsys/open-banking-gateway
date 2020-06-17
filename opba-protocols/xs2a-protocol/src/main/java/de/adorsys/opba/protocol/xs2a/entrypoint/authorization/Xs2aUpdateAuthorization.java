package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraAuthRequestParam;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common.AuthorizationContinuationService;
import de.adorsys.opba.protocol.xs2a.entrypoint.authorization.common.UpdateAuthMapper;
import de.adorsys.opba.protocol.xs2a.service.ContextUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.PSU_PASSWORD;
import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.SCA_CHALLENGE_DATA;
import static de.adorsys.opba.protocol.api.dto.parameters.ScaConst.SCA_CHALLENGE_ID;

/**
 * Entry point to update context with the input from user and continue BPMN process by triggering it.
 */
@Service("xs2aUpdateAuthorization")
@RequiredArgsConstructor
public class Xs2aUpdateAuthorization implements UpdateAuthorization {

    private final AuthorizationContinuationService continuationService;
    private final ContextUpdateService ctxUpdater;
    private final ExtendWithServiceContext extender;
    private final UpdateAuthMapper mapper;

    @Override
    public CompletableFuture<Result<UpdateAuthBody>> execute(ServiceContext<AuthorizationRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();
        ctxUpdater.updateContext(
            executionId,
            (Xs2aContext toUpdate) -> {
                toUpdate = mapper.updateContext(toUpdate, serviceContext.getRequest());
                updateWithExtras(toUpdate, serviceContext.getRequest().getExtras());
                updateWithScaChallenges(toUpdate, serviceContext.getRequest().getScaAuthenticationData());
                toUpdate = extender.extend(toUpdate, serviceContext);
                return toUpdate;
            }
        );

        return continuationService.handleAuthorizationProcessContinuation(executionId);
    }

    private void updateWithExtras(Xs2aContext context, Map<ExtraAuthRequestParam, Object> extras) {
        if (null == extras) {
            return;
        }

        if (extras.containsKey(ExtraAuthRequestParam.PSU_ID)) {
            context.setPsuId((String) extras.get(ExtraAuthRequestParam.PSU_ID));
        }

        if (extras.containsKey(ExtraAuthRequestParam.PSU_IP_ADDRESS)) {
            context.setPsuIpAddress((String) extras.get(ExtraAuthRequestParam.PSU_IP_ADDRESS));
        }

        if (extras.containsKey(ExtraAuthRequestParam.PSU_IP_PORT)) {
            context.setPsuIpPort((String) extras.get(ExtraAuthRequestParam.PSU_IP_PORT));
        }
    }

    private void updateWithScaChallenges(Xs2aContext context, Map<String, String> scaChallenges) {
        if (null == scaChallenges) {
            return;
        }

        if (null != scaChallenges.get(PSU_PASSWORD)) {
            context.setPsuPassword(scaChallenges.get(PSU_PASSWORD));
        }

        if (null != scaChallenges.get(SCA_CHALLENGE_DATA)) {
            context.setLastScaChallenge(scaChallenges.get(SCA_CHALLENGE_DATA));
        }

        if (null != scaChallenges.get(SCA_CHALLENGE_ID)) {
            context.setUserSelectScaId(scaChallenges.get(SCA_CHALLENGE_ID));
        }
    }
}


