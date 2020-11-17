package de.adorsys.opba.protocol.xs2a.tests.e2e;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor") // Lombok generates private ctor.
public class LocationExtractorUtil {

    @SneakyThrows
    public static String getLocation(ExtractableResponse<Response> response) {
        String location = response.header(LOCATION);
        assertThat(location).withFailMessage("Uninterpolated brace found").doesNotContain("{");
        assertThat(location).withFailMessage("Uninterpolated brace found").doesNotContain("}");
        assertThat(location).withFailMessage("Uninterpolated brace found").doesNotContain("%7B");
        assertThat(location).withFailMessage("Uninterpolated brace found").doesNotContain("%7D");
        return location;
    }
}
