package de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class ParsingUtil {

    public static final Document SYNTAX = DocumentFactory.createDocument("300");
    public static final Set<String> NON_SENSITIVE_FIELDS =
            fieldDefinitions("hbci-non-sensitive-fields.txt")
                    .stream()
                    .flatMap(it -> generateFromStarsRange100(it).stream())
                    .collect(Collectors.toSet());

    public static final Set<String> SENSITIVE_FIELDS = fieldDefinitions("hbci-sensitive-fields.txt")
            .stream()
            .flatMap(it -> generateFromStarsRange100(it).stream())
            .collect(Collectors.toSet());

    public Message parseMessage(String from) {
        return parseMessage(from, false);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    // This is the way original parser works - try - catch if message not matches - continue
    public Message parseMessage(String from, boolean failIfFieldsRemain) {
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

    @NotNull
    public String cleanupCryptoHeaders(String messageStr) {
        // Remove crypto-headers
        messageStr = messageStr
                .replaceAll("(?s)HNVSK.+?'", "")
                .replaceAll("(?s)HNVSD.+?@.+?@", "")
                .replaceAll("(?s)HNSHK.+?'", "");

        // Fix dangling values
        return messageStr
                .replaceAll("(?s)(HKSPA:\\d:\\d)'", "$1\\+'")
                .replaceAll("(?s)(HNSHA:\\d:\\d)", "'$1");
    }

    private Set<String> generateFromStarsRange100(String str) {
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

    @SneakyThrows
    private Set<String> fieldDefinitions(String resourceName) {
        return new HashSet<>(Resources.readLines(Resources.getResource(resourceName), StandardCharsets.UTF_8));
    }
}
