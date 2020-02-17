package de.adorsys.opba.fintech.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    protected String getFilenameBankProfile(String bankUUID) {
        return BANK_PROFILE_RESPONSE_PREFIX
                + "-" + bankUUID
                + POSTFIX;
    }

    protected final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
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
