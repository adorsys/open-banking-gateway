package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Redirect;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.Response;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ValidationIssue;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ProcessEventHandlerRegistrar {

    private final ProcessResultEventHandler handler;

    @Deprecated // FIXME - kept only for tests using endpoints
    public <T> void addHandler(String processId,
                               Consumer<Response> onSuccess,
                               CompletableFuture<ResponseEntity<T>> result
    ) {
        handler.add(
                processId,
                procResult -> {
                    if (procResult instanceof Response) {
                        doSuccess(onSuccess, (Response) procResult);
                    } else if (procResult instanceof Redirect) {
                        doRedirect(result, (Redirect) procResult);
                    } else if (procResult instanceof ValidationIssue) {
                        doFixValidation(result, (ValidationIssue) procResult);
                    }
                });
    }

    public <T> void addHandler(String processId, OutcomeMapper<T> mapper) {
        handler.add(
                processId,
                procResult -> {
                    if (procResult instanceof Response) {
                        mapper.onSuccess((Response) procResult);
                    } else if (procResult instanceof Redirect) {
                        mapper.onRedirect((Redirect) procResult);
                    } else if (procResult instanceof ValidationIssue) {
                        mapper.onValidationProblem((ValidationIssue) procResult);
                    } else {
                        mapper.onError();
                    }
                });
    }

    private <T> void doSuccess(Consumer<Response> onSuccess, Response procResult) {
        onSuccess.accept(procResult);
    }

    private <T> void doRedirect(CompletableFuture<ResponseEntity<T>> result, Redirect redirResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirResult.getRedirectUri());
        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
    }

    @SneakyThrows
    private <T> void doFixValidation(CompletableFuture<ResponseEntity<T>> result, ValidationIssue validResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(validResult.getProvideMoreParamsDialog());
        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
    }
}
