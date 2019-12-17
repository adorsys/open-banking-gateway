package de.adorsys.opba.core.protocol.service.eventbus;

import de.adorsys.opba.core.protocol.domain.dto.RedirectResult;
import de.adorsys.opba.core.protocol.domain.dto.ResponseResult;
import lombok.RequiredArgsConstructor;
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
                        onSuccess.accept((ResponseResult) procResult);
                    } else if (procResult instanceof RedirectResult) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setLocation(URI.create(((RedirectResult) procResult).getRedirectUri()));
                        result.complete(new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY));
                    } else {
                        result.complete(ResponseEntity.badRequest().build());
                    }
                });
    }
}
