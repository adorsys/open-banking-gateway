package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class HbciStubGenerator {

    private static final Random RANDOM = new Random();

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
    void generateImpersonatedStub() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/dissect/2-response.txt"); // Replace with your fixture path
        String messageStr = new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                .replaceAll("\n", "'")
                .replace("'$", "")
                // Remove crypto-headers
                .replaceAll("HNVSK.+?'", "")
                .replaceAll("HNVSD.+?'", "");

        // contains all values that were replaced by if their length is more than 4 chars.
        // If value occurs in one field and then same in another - they should be obfuscated with same value.
        Map<String, String> replacedValuesCache = new HashMap<>();
        Message msg = parseMessage(messageStr, true);
        Set<String> sensitiveFields = Sets.intersection(msg.getData().keySet(), SENSITIVE_FIELDS);

        for (String sensitive : sensitiveFields) {
            String value = msg.getData().get(sensitive);
            if (value.length() >= 4) {
                handleCacheableSensitiveValue(replacedValuesCache, msg, sensitive, value);
                continue;
            }

            String newValue = generateObfuscatedValue(value);
            setValue(msg, sensitive, newValue);
        }

        log.info("========================= GENERATED ==================================");
        Arrays.stream(msg.toString(0).split("'")).forEach(it -> log.info("{}", it));
        assertThat(msg).isNotNull();
    }

    private void setValue(Message msg, String path, String newValue) {
        msg.propagateValue(msg.getPath() + "." + path, newValue, true, true);
    }

    private void handleCacheableSensitiveValue(Map<String, String> replacedValuesCache, Message msg, String sensitive, String value) {
        String cached = replacedValuesCache.get(value);
        if (null != cached) {
            setValue(msg, sensitive, cached);
            return;
        }

        String newValue = generateObfuscatedValue(value);
        setValue(msg, sensitive, newValue);
        replacedValuesCache.put(value, newValue);
    }

    private String generateObfuscatedValue(String original) {
        if (null != Ints.tryParse(original)) {
            return String.valueOf(RANDOM.nextInt(Integer.parseInt(original)));
        }

        byte[] buffer = new byte[original.length()];
        RANDOM.nextBytes(buffer);
        return BaseEncoding.base64Url().omitPadding().encode(buffer).substring(0, original.length());
    }

    /**
     * This test simply classifies input message.
     */
    @Test
    @Disabled
    @SneakyThrows
    void classifyMessage() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/dissect/2-response.txt");
        parseMessage(readMessage(target));
    }

    @NotNull
    private String readMessage(Path target) throws IOException {
        return new String(Files.asByteSource(target.toFile()).read(), StandardCharsets.ISO_8859_1)
                .replaceAll("\n", "'")
                .replace("'$", "")
                // Remove crypto-headers
                .replaceAll("HNVSK.+?'", "")
                .replaceAll("HNVSD.+?'", "");
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

    private Message parseMessage(String from) {
        return parseMessage(from, false);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    // This is the way original parser works - try - catch if message not matches - continue
    private Message parseMessage(String from, boolean failIfFieldsRemain) {
        NodeList list = SYNTAX.getElementsByTagName("MSGdef");
        AtomicReference<Message> result = new AtomicReference<>();
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
                        if (failIfFieldsRemain && !keys.isEmpty()) {
                            throw new IllegalStateException("Fields were left");
                        }
                        result.set(msg);
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