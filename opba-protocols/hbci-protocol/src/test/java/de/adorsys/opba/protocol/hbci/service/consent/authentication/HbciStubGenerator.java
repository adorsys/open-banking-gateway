package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class HbciStubGenerator {

    private static final Document SYNTAX = DocumentFactory.createDocument("300");
    private static final Set<String> NON_SENSITIVE_FIELDS =
            fieldDefinitions("hbci-non-sensitive-fields.txt")
                    .stream()
                    .flatMap(it -> generateFromStarsRange100(it).stream())
                    .collect(Collectors.toSet());

    private static final Set<String> SENSITIVE_FIELDS = fieldDefinitions("hbci-sensitive-fields.txt")
            .stream()
            .flatMap(it -> generateFromStarsRange100(it).stream())
            .collect(Collectors.toSet());

    /**
     * This test takes HBCI dialog (multiple request-response) that may contain sensitive data and produces
     * safe version of it. Only HBCI tags (HNBNK, HNSHA...) and their order are kept and their parameters are replaced
     * with dummy ones.
     */
    @Test
    @SneakyThrows
    @Disabled // TODO finish it with OBG-693
    void generateImpersonatedStub() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/hbci-ag-mock/sparda/sync-my-temp.txt"); // Replace with your fixture path
        String type = message(
                new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                        .replaceAll("\n", "'")
                        .replace("'$", "")
        );

        assertThat(type).isNotNull();
    }

    /**
     * This test simply classifies input message.
     */
    @Test
    @Disabled
    @SneakyThrows
    void classifyMessage() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/hbci-ag-mock/sparda/sepaInfo.txt");

        classifyMessageType(
                new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                        .replaceAll("\n", "'")
                        .replace("'$", "")
        );
    }

    private static Set<String> generateFromStarsRange100(String str) {
        int stars = str.split("\\*", -1).length - 1;
        if (stars == 0) {
            return ImmutableSet.of(str);
        }

        Set<String> result = new LinkedHashSet<>();
        int starRange = 2;
        int max = BigDecimal.ONE.movePointRight(starRange * stars).intValueExact();
        for (int i = 0; i < max; i++) {
            String res = str;
            int value = i;
            for (int indStar = 0; indStar < stars; indStar++) {
                int div = BigDecimal.ONE.movePointRight(starRange * (stars - indStar - 1)).intValueExact();
                res = res.replaceFirst("\\*", "" + (value / div + 1));
                value -= (value / div) * div;
            }
            result.add(res);
        }

        return result;
    }

    private String message(String from) {
        return classifyMessageType(from);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock") // This is the way original parser works - try - catch if message not matches - continue
    private String classifyMessageType(String from) {
        NodeList list = SYNTAX.getElementsByTagName("MSGdef");
        AtomicReference<String> result = new AtomicReference<>();
        IntStream.range(0, list.getLength()).mapToObj(list::item)
                .map(it -> (Element) it)
                .forEach(node -> {
                    String msgName = node.getAttribute("id");
                    try {
                        Message msg = new Message(msgName, from, SYNTAX, false, true);
                        Set<String> keys = new HashSet<>(msg.getData().keySet());
                        int size = keys.size();
                        keys.removeAll(NON_SENSITIVE_FIELDS);
                        log.info("=================================== {} ===================================", msgName);
                        log.info("Found {} insensitive fields", size - keys.size());
                        size = keys.size();
                        keys.removeAll(SENSITIVE_FIELDS);
                        log.info("Found {} SENSITIVE fields", size - keys.size());
                        keys.forEach(it -> log.info("Found UNKNOWN FIELD: {}={}", it, msg.getData().get(it)));
                        log.info("============================================================================");
                    } catch (RuntimeException ex) {
                        // NOP
                    }
                });

        return result.get();
    }

    @SneakyThrows
    private static Set<String> fieldDefinitions(String resourceName) {
        return new HashSet<>(Resources.readLines(Resources.getResource(resourceName), StandardCharsets.UTF_8));
    }
}