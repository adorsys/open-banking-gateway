package de.adorsys.opba.protocol.hbci.service.consent.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class HbciStubGenerator {

    private static final int MINIMUM_LENGTH_FOR_UNIQUENESS = 3;
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
     * This test takes HBCI log file and creates desaturated messages out of it.
     */
    @Test
    @Disabled
    @SneakyThrows
    void generateDesaturated() {
        Path sourceFile = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/data/multibanking-test.txt");
        Path destinationFolder = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/obfuscated/");

        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        Map<Integer, String> messagesByPos = extractHbciMessageBlocks(new String(Files.readAllBytes(sourceFile), StandardCharsets.UTF_8));
        Map<Integer, Map<String, String>> desaturatedMessageByPos = new TreeMap<>();
        for (Map.Entry<Integer, String> message : messagesByPos.entrySet()) {
            Message parsed = parseMessage(cleanupCryptoHeaders(message.getValue()));
            Map<String, String> data = new TreeMap<>(parsed.getData());
            data.put("A_TYPE", parsed.getName());
            desaturatedMessageByPos.put(message.getKey(), data);
        }

        log.info("{}", writer.writeValueAsString(desaturatedMessageByPos));
    }

    private Map<Integer, String> extractHbciMessageBlocks(String from) {
        Map<Integer, String> extractedMessages = new TreeMap<>();
        Pattern blockPattern = Pattern.compile("(HNHBK:.+?(HNHBS:\\d+:\\d+\\+\\d+))", Pattern.DOTALL);
        Pattern chunkPattern = Pattern.compile("([A-Z]{5,6}:\\d+:\\d+.+?)([A-Z]{5,6}:\\d+:\\d+)", Pattern.DOTALL);
        Matcher blockMatcher = blockPattern.matcher(from);
        int pos = 0;
        while (blockMatcher.find()) {
            String blockChunk = blockMatcher.group(1);
            Matcher messageMatcher = chunkPattern.matcher(blockChunk);
            StringBuilder resultMessage = new StringBuilder();
            int chunkPos = 0;
            while (messageMatcher.find(chunkPos)) {
                chunkPos = parseMessage(messageMatcher, resultMessage);
            }

            resultMessage.append("'");
            resultMessage.append(blockMatcher.group(2));
            extractedMessages.put(pos, resultMessage.toString());
            pos++;
        }

        return extractedMessages;
    }

    private int parseMessage(Matcher messageMatcher, StringBuilder resultMessage) {
        Pattern binPattern = Pattern.compile("@(\\d+)@");
        int chunkPos;
        String message = messageMatcher.group(1);
        Matcher binMatcher = binPattern.matcher(message);
        // truncate non-binary messages
        if (!binMatcher.find()) {
            message = message.split("[\r\n]")[0];
        } else {
            int len = Integer.parseInt(binMatcher.group(1));
            int binEnd = binMatcher.end(1) + len;
            if (binEnd < message.length()) {
                message = message.substring(0, binEnd) + message.substring(binEnd + 1).split("[\r\n]")[0];
            }
        }

        resultMessage.append(message);
        resultMessage.append("'");
        chunkPos = messageMatcher.start(2);
        return chunkPos;
    }

    /**
     * This test takes HBCI dialog (multiple request-response) that may contain sensitive data and produces
     * safe version of it. Only HBCI tags (HNBNK, HNSHA...) and their order are kept and their parameters are replaced
     * with dummy ones.
     */
    @Test
    @Disabled
    @SneakyThrows
    void generateImpersonatedStub() {
        Map<String, String> replacedValuesCache = new HashMap<>();
        Path source = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/dissect/");
        Path destination = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/obfuscated/");

        try (Stream<Path> files = Files.walk(source)) {
            files.filter(it -> it.toFile().isFile()).forEach(sourceFile -> createObfuscatedFile(destination, sourceFile, replacedValuesCache));
        }
    }

    @SneakyThrows
    private void createObfuscatedFile(Path destination, Path sourceFile, Map<String, String> replacedValuesCache) {
        log.info("Obfuscating {}", sourceFile);
        Message obfuscated = obfuscateMessage(sourceFile, replacedValuesCache);
        Path targetFile = destination.resolve(sourceFile.getFileName());
        if (isRaw(sourceFile)) {
            Files.write(targetFile, obfuscated.toString(0).getBytes(StandardCharsets.UTF_8));
            return;
        }

        Files.write(targetFile, obfuscated.toString(0).replaceAll("'", "\n").getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private Message obfuscateMessage(Path messageFile, Map<String, String> replacedValuesCache) {
        // contains all values that were replaced by if their length is more than 4 chars.
        // If value occurs in one field and then same in another - they should be obfuscated with same value.
        Message msg = parseMessage(readMessage(messageFile), true);
        dumpMessage(msg);
        Set<String> sensitiveFields = Sets.intersection(msg.getData().keySet(), SENSITIVE_FIELDS);

        for (String sensitive : sensitiveFields) {
            String value = msg.getData().get(sensitive);
            if (value.length() >= MINIMUM_LENGTH_FOR_UNIQUENESS) {
                handleCacheableSensitiveValue(replacedValuesCache, msg, sensitive, value);
                continue;
            }

            String newValue = generateObfuscatedValue(sensitive, value);
            setValue(msg, sensitive, newValue);
        }
        return msg;
    }

    @SneakyThrows
    private void dumpMessage(Message msg) {
        log.info("Desaturated message {}:", msg.getName());
        Map<String, String> data = new TreeMap<>(msg.getData());
        data.put("TYPE", msg.getName());
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data));
    }

    private boolean isRaw(Path messageFile) {
        return messageFile.getFileName().toString().contains("-raw");
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

        if (sensitive.contains("KUmsZeitRes5") && sensitive.contains("booked")) {
            handleTransactionResponseBody(replacedValuesCache, msg, sensitive, value);
            return;
        }

        String newValue = generateObfuscatedValue(sensitive, value);
        setValue(msg, sensitive, newValue);
        replacedValuesCache.put(value, newValue);
    }

    private void handleTransactionResponseBody(Map<String, String> replacedValuesCache, Message msg, String sensitive, String value) {
        Pattern pattern = Pattern.compile("(\\d{3,})");
        Matcher numberMatcher = pattern.matcher(value);
        String obfuscatedValue = value;
        while (numberMatcher.find()) {
            String numValue = numberMatcher.group(1);

            String cached = replacedValuesCache.get(numValue);
            if (null != cached) {
                obfuscatedValue = obfuscatedValue.replaceAll(numValue, cached);
                continue;
            }

            Long val = randomNumberOfSameRadixSize(numValue);
            if (null == val) {
                continue;
            }

            obfuscatedValue = obfuscatedValue.replaceAll(numValue, val.toString());
        }

        setValue(msg, sensitive, "B" + obfuscatedValue);
        replacedValuesCache.put(value, obfuscatedValue);
    }

    private String generateObfuscatedValue(String keyName, String original) {
        if (keyName.contains("iban")) {
            return new Iban.Builder().countryCode(CountryCode.DE).buildRandom().toString();
        }

        Long val = randomNumberOfSameRadixSize(original);
        if (null != val) {
            return val.toString();
        }

        byte[] buffer = new byte[original.length()];
        RANDOM.nextBytes(buffer);
        String randomString = BaseEncoding.base64Url().omitPadding().encode(buffer).substring(0, original.length());

        // Keep spaces for traceability
        int pos = 0;
        while ((pos = original.indexOf(' ', pos)) >= 0) {
            randomString = randomString.substring(0, pos) + ' ' + randomString.substring(pos);
            pos += 1;
        }

        return randomString;
    }

    private Long randomNumberOfSameRadixSize(String original) {
        try {
            long value = Long.parseLong(original);
            double logValue = Math.log10(value);
            long min = (long) Math.pow(10.0, (int) logValue);
            long max = (long) Math.pow(10.0, (int) (logValue + 1.0));
            return min + (long)(RANDOM.nextDouble() * (max - min));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * This test simply classifies input message.
     */
    @Test
    @Disabled
    @SneakyThrows
    void classifyMessage() {
        Path target = Paths.get("/home/valb3r/IdeaProjects/mock-hbci-mhr/dissect/7-request.txt");
        assertThat(parseMessage(readMessage(target))).isNotNull();
    }

    @SneakyThrows
    private String readMessage(Path messageFile) {
        String messageStr = new String(Files.readAllBytes(messageFile), StandardCharsets.UTF_8);

        if (isRaw(messageFile)) {
            messageStr = messageStr.replaceAll("\n", "\r\n");
        } else {
            messageStr = messageStr.replaceAll("\n", "'");
        }

        return cleanupCryptoHeaders(messageStr);
    }

    @NotNull
    private String cleanupCryptoHeaders(String messageStr) {
        // Remove crypto-headers
        messageStr = messageStr
                .replaceAll("HNVSK.+?'", "")
                .replaceAll("HNVSD.+?@.+?@", "")
                .replaceAll("HNSHK.+?'", "");

        // Fix dangling HKSPA
        return messageStr.replaceAll("(HKSPA:\\d:\\d)'", "$1\\+'");
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
        Message result = null;
        for (int i = 0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
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

                // End loop on 1st element
                result = msg;
                break;
            } catch (RuntimeException ex) {
                // NOP, that's how kapott works
            }
        }

        return result;
    }

    @SneakyThrows
    private static Set<String> fieldDefinitions(String resourceName) {
        return new HashSet<>(Resources.readLines(Resources.getResource(resourceName), StandardCharsets.UTF_8));
    }
}