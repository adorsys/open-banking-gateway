package de.adorsys.opba.core.protocol.service.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.core.protocol.domain.dto.RedirectResult;
import de.adorsys.opba.core.protocol.domain.dto.ResponseResult;
import de.adorsys.opba.core.protocol.domain.dto.ValidationIssueResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.VALIDATIONS_RESULT_HEADER;

@Service
@RequiredArgsConstructor
public class ProcessEventHandlerRegistrar {

    private final ObjectMapper mapper;
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
                    } else {
                        doHandleOther(result);
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
        headers.set(VALIDATIONS_RESULT_HEADER, mapper.writeValueAsString(validResult.getViolations()));
        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
    }

    private <T> void doHandleOther(CompletableFuture<ResponseEntity<T>> result) {
        result.complete(ResponseEntity.badRequest().build());
    }
}
