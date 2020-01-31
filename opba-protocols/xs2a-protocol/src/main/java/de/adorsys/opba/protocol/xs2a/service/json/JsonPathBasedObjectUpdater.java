package de.adorsys.opba.protocol.xs2a.service.json;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jayway.jsonpath.Option.DEFAULT_PATH_LEAF_TO_NULL;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;

// TODO find some lib to do this...
@Service
@RequiredArgsConstructor
public class JsonPathBasedObjectUpdater {

    private final Configuration jsonConfig = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .options(DEFAULT_PATH_LEAF_TO_NULL, SUPPRESS_EXCEPTIONS)
            .build();

    private final ObjectMapper mapper;
    private final ProtocolConfiguration configuration;

    @SneakyThrows
    public <T> T updateObjectUsingJsonPath(T object, Map<String, String> jsonPathToItsValue) {
        TreeNode tree = mapper.valueToTree(object);
        DocumentContext docCtx = JsonPath.parse(tree, jsonConfig);
        Map<String, Integer> foundArraySizes = new HashMap<>();

        jsonPathToItsValue.forEach((path, value) -> analyzeArraySizes(path, docCtx, foundArraySizes));
        jsonPathToItsValue.forEach((path, value) -> safeSet(path, docCtx, value, foundArraySizes));
        return (T) mapper.treeToValue(tree, object.getClass());
    }

    private void analyzeArraySizes(String path, DocumentContext ctx, Map<String, Integer> foundArraySize) {
        visitPathParents(
                path,
                ctx,
                objPath -> { },
                (arrPath, arrDesc) -> foundArraySize.compute(
                        arrPath, (id, val) -> null == val
                                ? arrDesc.getMinArraySize() : Math.max(val, arrDesc.getMinArraySize())
                )
        );
    }
    private void safeSet(String path, DocumentContext ctx, Object value, Map<String, Integer> foundArraySizes) {
        visitPathParents(
                path,
                ctx,
                objPath -> ctx.set(objPath, new HashMap<>()),
                (arrPath, arrDesc) -> ctx.set(
                        arrPath,
                        IntStream.range(0, foundArraySizes.get(arrPath)).boxed()
                                .map(it -> new HashMap<>())
                                .collect(Collectors.toList())
                )
        );

        ctx.set(path, value);
    }

    private void visitPathParents(String path, DocumentContext ctx,
                                  Consumer<String> missingParentObjectHandler,
                                  BiConsumer<String, ArraySegment> missingArrayHandler) {
        path = path.replaceAll("([A-Za-z0-9-_.]+)\\['(.+)']", "$1.$2");
        String[] segments = path.replaceAll("^\\$\\.", "").split("\\.");

        String pointer = "$";
        for (int i = 0; i < segments.length - 1; ++i) {
            String currentSegment = segments[i];
            String prevPointer = pointer;
            pointer = pointer + "." + currentSegment;
            Object result = ctx.read(pointer);

            if (result instanceof NullNode || null == result) {
                ArraySegment asArray = tryParseArraySegment(currentSegment);
                if (null != asArray) {
                    missingArrayHandler.accept(prevPointer + "." + asArray.getName(), asArray);
                    continue;
                }
                missingParentObjectHandler.accept(pointer);
            }
        }
    }

    private ArraySegment tryParseArraySegment(String segment) {
        Pattern pattern = Pattern.compile("(.+)\\[([0-9]+)]");
        Matcher matcher = pattern.matcher(segment);
        if (!matcher.matches()) {
            return null;
        }

        int position = Integer.parseInt(matcher.group(2));
        if (position >= configuration.getRedirect().getParameters().getMaxArraySize()) {
            throw new IllegalArgumentException("Array size is too large: " + segment);
        }

        return new ArraySegment(
                matcher.group(1),
                position + 1
        );
    }

    @Data
    @AllArgsConstructor
    private static class ArraySegment {

        private final String name;
        private final int minArraySize;
    }
}
