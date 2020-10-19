package de.adorsys.opba.protocol.bpmnshared.service.context;

import de.adorsys.opba.protocol.bpmnshared.GlobalConst;
import de.adorsys.opba.protocol.bpmnshared.dto.context.BaseContext;
import lombok.experimental.UtilityClass;
import org.flowable.engine.delegate.DelegateExecution;
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

    /**
     * Allows to perform string interpolation like '/ais/{sessionId}' using the process context. Appends redirectCode
     * if necessary.
     */
    public URI buildAndExpandQueryParameters(String urlTemplate, BaseContext context, String redirectCode, String scaType) {
        Map<String, String> expansionContext = new HashMap<>();

        expansionContext.put("sessionId", context.getAuthorizationSessionIdIfOpened());
        expansionContext.put("wrong", null == context.getWrongAuthCredentials() ? null : context.getWrongAuthCredentials().toString());
        expansionContext.put("userSelectScaType", scaType);

        URI uri = UriComponentsBuilder.fromHttpUrl(urlTemplate)
                .buildAndExpand(expansionContext)
                .toUri();

        if (redirectCode != null) {
            uri = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("redirectCode", redirectCode)
                    .build()
                    .toUri();
        }
        return uri;
    }
}
