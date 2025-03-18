package de.adorsys.opba.fintech.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.adorsys.opba.fintech.impl.tppclients.Consts;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static de.adorsys.opba.fintech.server.FinTechBankSearchApiTest.FIN_TECH_AUTH_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class FinTechApiBaseTest {
    private static final String BANK_SEARCH_RESPONSE_PREFIX = "TPP_BankSearchResponse";
    private static final String BANK_PROFILE_RESPONSE_PREFIX = "TPP_BankProfileResponse";
    private static final String POSTFIX = ".json";

    @SneakyThrows
    protected String readFile(String fileName) {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    protected String getFilenameBankSearch(String keyword, Integer start, Integer max) {
        return BANK_SEARCH_RESPONSE_PREFIX
                + "-" + keyword
                + "-" + start
                + "-" + max
                + POSTFIX;
    }

    protected String getFilenameBankProfile(UUID bankUUID) {
        return BANK_PROFILE_RESPONSE_PREFIX
                + "-" + bankUUID
                + POSTFIX;
    }

    @SneakyThrows
    protected MvcResult plainAuth(MockMvc mvc, String username, String password) {
        LoginBody loginBody = new LoginBody(username, password);
        return mvc.perform(
                        post(FIN_TECH_AUTH_URL)
                                .header(Consts.HEADER_X_REQUEST_ID, UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(GSON.toJson(loginBody))
                )
                .andDo(print())
                .andReturn();
    }

    protected final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }

        @Override
        public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return LocalDate.parse(jsonElement.getAsString());
        }
    }
}
