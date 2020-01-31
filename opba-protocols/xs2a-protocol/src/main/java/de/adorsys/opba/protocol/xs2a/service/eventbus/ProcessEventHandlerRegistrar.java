package de.adorsys.opba.protocol.xs2a.service.eventbus;

import de.adorsys.opba.protocol.xs2a.domain.dto.messages.RedirectResult;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ResponseResult;
import de.adorsys.opba.protocol.xs2a.domain.dto.messages.ValidationIssueResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ProcessEventHandlerRegistrar {

    private final ProcessResultEventHandler handler;

    public <T> void addHandler(String processId,
                           Consumer<ResponseResult> onSuccess,
                           CompletableFuture<ResponseEntity<T>> result
    ) {
        handler.add(
                processId,
                procResult -> {
                    if (procResult instanceof ResponseResult) {
                        doSuccess(onSuccess, (ResponseResult) procResult);
                    } else if (procResult instanceof RedirectResult) {
                        doRedirect(result, (RedirectResult) procResult);
                    } else if (procResult instanceof ValidationIssueResult) {
                        doFixValidation(result, (ValidationIssueResult) procResult);
                    }
                });
    }

    private <T> void doSuccess(Consumer<ResponseResult> onSuccess, ResponseResult procResult) {
        onSuccess.accept(procResult);
    }

    private <T> void doRedirect(CompletableFuture<ResponseEntity<T>> result, RedirectResult redirResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirResult.getRedirectUri()));
        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
    }

    @SneakyThrows
    private <T> void doFixValidation(CompletableFuture<ResponseEntity<T>> result, ValidationIssueResult validResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(validResult.getProvideMoreParamsDialog());
        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
    }
}
