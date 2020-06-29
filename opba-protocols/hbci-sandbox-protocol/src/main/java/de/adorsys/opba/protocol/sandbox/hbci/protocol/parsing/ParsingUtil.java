package de.adorsys.opba.protocol.sandbox.hbci.protocol.parsing;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.kapott.hbci.manager.DocumentFactory;
import org.kapott.hbci.protocol.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Slf4j
@UtilityClass
public class ParsingUtil {

    public static final Document SYNTAX = DocumentFactory.createDocument("300");

    public Message parseMessageWithoutSensitiveNonSensitiveValidation(String from) {
        return parseMessageWithoutSensitiveNonSensitiveValidation(from, false);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    // This is the way original parser works - try - catch if message not matches - continue
    public Message parseMessageWithoutSensitiveNonSensitiveValidation(String from, boolean failIfFieldsRemain) {
        NodeList list = SYNTAX.getElementsByTagName("MSGdef");
        Message result = null;
        for (int i = 0; i < list.getLength(); i++) {
            Element node = (Element) list.item(i);
            String msgName = node.getAttribute("id");
            try {
                // End loop on 1st element
                result = new Message(msgName, from, SYNTAX, false, true);
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
}
