package de.adorsys.opba.protocol.bpmnshared.service.context;

import de.adorsys.opba.protocol.bpmnshared.GlobalConst;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class to work with Flowable BPMN engine process context.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class ContextUtil {

    /**
     * Read context from current execution.
     */
    public <T> T getContext(DelegateExecution execution, Class<T> ctxType) {
        return execution.getVariable(GlobalConst.CONTEXT, ctxType);
    }

    /**
     * Get and update context of current execution in single operation with retry support for optimistic exceptions.
     */
    public <T> void getAndUpdateContext(DelegateExecution execution, Consumer<T> contextUpdater) {
        @SuppressWarnings("unchecked")
        T ctx = (T) execution.getVariable(GlobalConst.CONTEXT);
        contextUpdater.accept(ctx);
        execution.setVariable(GlobalConst.CONTEXT, ctx);
    }

    public URI buildAndExpandQueryParameters(String urlTemplate, BaseContext context) {
        return buildAndExpandQueryParameters(urlTemplate, context, Mappers.getMapper(DefaultContextMapper.class));
    }

    public URI buildAndExpandQueryParameters(String urlTemplate, BaseContext context, ContextMapper mapper) {
        return buildAndExpandQueryParameters(urlTemplate, mapper.map(context));
    }

    /**
     * Allows to perform string interpolation like '/ais/{sessionId}' using the process context.
     */
    public URI buildAndExpandQueryParameters(String urlTemplate, UrlContext context) {
        Map<String, String> expansionContext = new HashMap<>();

        expansionContext.put("authSessionId", context.getAuthSessionId());
        expansionContext.put("selectedScaType", context.getSelectedScaType());
        expansionContext.put("redirectCode", context.getRedirectCode());
        expansionContext.put("aspspRedirectCode", context.getAspspRedirectCode());
        expansionContext.put("isWrongCreds", null == context.getIsWrongCreds() ? null : context.getIsWrongCreds().toString());

        return UriComponentsBuilder.fromHttpUrl(urlTemplate)
                .buildAndExpand(expansionContext)
                .toUri();
    }

    @Data
    public static class UrlContext {

        private String authSessionId;
        private String selectedScaType;
        private String redirectCode;
        private String aspspRedirectCode;
        private Boolean isWrongCreds;
    }

    @FunctionalInterface
    public interface ContextMapper {

        UrlContext map(BaseContext context);
    }

    @Mapper
    public interface DefaultContextMapper extends ContextMapper {

        @Mapping(source = "authorizationSessionIdIfOpened", target = "authSessionId")
        @Mapping(source = "redirectCodeIfAuthContinued", target = "redirectCode")
        @Mapping(source = "wrongAuthCredentials", target = "isWrongCreds")
        UrlContext map(BaseContext context);
    }
}
