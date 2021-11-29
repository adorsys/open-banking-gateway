package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.AUTHORIZATION_SESSION_KEY;
import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * Used for tests without OBG Consent UI, as OBG responds with 202 instead of 301, this transformer redirects browser
 * to correct place.
 */
@Setter
public class RedirectCapturingTransformer extends ResponseDefinitionTransformer {

    private int obgPort;
    private String authCookie;

    @Override
    @SneakyThrows
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(UriComponentsBuilder.fromHttpUrl(request.getAbsoluteUrl()).port(obgPort).build().toUri())
                .header("Cookie", AUTHORIZATION_SESSION_KEY + "=" + authCookie)
                .GET()
                .build();
        var client = HttpClient.newHttpClient();
        var resp = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return new ResponseDefinitionBuilder()
                .withHeader(LOCATION, resp.headers().firstValue(LOCATION).orElseThrow())
                .withStatus(HttpStatus.TEMPORARY_REDIRECT.value())
                .build();
    }

    @Override
    public String getName() {
        return "example";
    }
}
