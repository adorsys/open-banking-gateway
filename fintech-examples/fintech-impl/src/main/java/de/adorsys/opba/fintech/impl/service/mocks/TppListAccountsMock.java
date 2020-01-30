package de.adorsys.opba.fintech.impl.service.mocks;

import com.google.gson.Gson;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TppListAccountsMock {

    private static final Gson GSON = new Gson();

    public AccountList getAccountList() {
        return GSON.fromJson(readFile("TPP_LIST_ACCOUNTS"), AccountList.class);
    }

    @SneakyThrows
    private String readFile(String fileName) {
        String mockValue = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8);
        log.info("MOCK DATA " + mockValue);
        return mockValue;
    }

}
