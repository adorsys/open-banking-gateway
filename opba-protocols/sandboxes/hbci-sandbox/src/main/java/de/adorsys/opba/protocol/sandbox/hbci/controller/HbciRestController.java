package de.adorsys.opba.protocol.sandbox.hbci.controller;

import com.google.common.base.Joiner;
import de.adorsys.opba.protocol.sandbox.hbci.service.HbciMockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/hbci-mock/*")
public class HbciRestController {

    private final HbciMockService hbciMockService;

    @PostMapping(produces = {MediaType.TEXT_PLAIN_VALUE}, consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public String hbciPostRequest(@RequestBody String requestEncoded) {
        return doHandle(requestEncoded);
    }

    @PostMapping(produces = {MediaType.TEXT_PLAIN_VALUE}, consumes = MediaType.TEXT_PLAIN_VALUE)
    public String hbciPostRequestPlain(@RequestBody String requestEncoded) {
        return doHandle(requestEncoded);
    }

    private String doHandle(@RequestBody String requestEncoded) {
        log.info("request: \nRQ-->:\n{}\n", decode(requestEncoded));
        String result = hbciMockService.handleRequest(requestEncoded);
        log.info("response: \nRS-->:\n{}\n", result);
        return encode(result);
    }

    private String decode(String encoded) {
        return Joiner.on("\n").join(new String(Base64.decodeBase64(encoded)).split("'"));
    }

    private String encode(String rawWithoutReturns) {
        return Base64.encodeBase64String(rawWithoutReturns.getBytes(StandardCharsets.ISO_8859_1));
    }
}
