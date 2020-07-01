package de.adorsys.opba.protocol.sandbox.hbci.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized.AuthenticatedDialogInitSca;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.kapott.hbci.protocol.Message;
import org.kapott.hbci.protocol.SyntaxElement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Operation {

    DIALOG_INIT_ANON("DialogInitAnon", Operation::typeMatch),
    DIALOG_INIT_SCA_TAN_2_STEP("DialogInitScaTAN", Operation::isScaInit),
    DIALOG_INIT_SCA("DialogInitSCA", Operation::typeMatchAndNotAfterSca),
    DIALOG_INIT("DialogInit", Operation::typeMatch),
    CUSTOM_MSG("CustomMsg", Operation::typeMatch),
    SYNCH("Synch", Operation::typeMatch),
    DIALOG_END("DialogEnd", Operation::typeMatch),
    ANY("*", Operation::typeMatch);

    private final String typeName;
    private final Function<MatchingContext, Message> is;

    public static Match find(List<Message> matched) {
        return Arrays.stream(Operation.values())
                .map(it -> {
                    Message message = it.getIs().apply(new MatchingContext(it.getTypeName(), matched));
                    return null == message ? null : new Match(it, message);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown operation to match: " + matchToString(matched)));
    }

    @SneakyThrows
    private static String matchToString(List<Message> matched) {
        return new ObjectMapper().writeValueAsString(matched);
    }

    private static Message typeMatchAndNotAfterSca(MatchingContext context) {
        Message message = typeMatch(context);
        if (null == message) {
            return null;
        }

        if (RequestStatusUtil.isForTransactionListing(message.getData())) {
            return null;
        }

        return message;
    }

    private static Message typeMatch(MatchingContext context) {
        return context.getMatched().stream()
                .filter(it -> it.getType().equals(context.getTypeName()))
                .findFirst()
                .orElse(null);
    }

    private static Message isScaInit(MatchingContext context) {
        Set<String> acceptableTypes = ImmutableSet.of(Operation.DIALOG_INIT.typeName, Operation.CUSTOM_MSG.typeName);
        Set<String> available = context.getMatched().stream().map(SyntaxElement::getType).collect(Collectors.toSet());
        if (Sets.intersection(acceptableTypes, available).isEmpty()) {
            return null;
        }

        return context.getMatched().stream()
                .filter(it -> AuthenticatedDialogInitSca.canHandle(it.getData()))
                .findFirst()
                .orElse(null);
    }

    @Data
    private static class MatchingContext {

        private final String typeName;
        private final List<Message> matched;
    }

    @Data
    public static class Match {

        private final Operation operation;
        private final Message message;
    }
}
