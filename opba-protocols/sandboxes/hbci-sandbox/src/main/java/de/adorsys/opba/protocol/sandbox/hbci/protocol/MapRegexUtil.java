package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public class MapRegexUtil {

    @JsonIgnore
    public String getDataRegex(Map<String, String> data, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return data.entrySet().stream()
                .filter(it -> pattern.matcher(it.getKey()).find()).findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
