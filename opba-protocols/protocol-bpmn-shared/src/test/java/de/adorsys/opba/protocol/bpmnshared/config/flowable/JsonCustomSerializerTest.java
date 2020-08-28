package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonCustomSerializerTest {
    private static JsonCustomSerializer serializer;
    private static ObjectMapper mapper;

    @BeforeAll
    @SneakyThrows
    public static void init() {
        mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsString(any())).thenAnswer(inv -> inv.getArgument(0, String.class));
        serializer = new JsonCustomSerializer(null, mapper, Collections.singletonList(String.class.getCanonicalName()), 2048);
    }

    @Test
    void serializeLessThanMaxValueTest() {
        String input = Strings.repeat("a", 2047);
        assertThat(serializer.isAbleToStore(input)).isTrue();
    }

    @Test
    void serializeEqualsToMaxValueTest() {
        String input = Strings.repeat("a", 2048);
        assertThat(serializer.isAbleToStore(input)).isTrue();
    }

    @Test
    void serializeMoreThanMaxValueTest() {
        String input = Strings.repeat("a", 2049);
        assertThat(serializer.isAbleToStore(input)).isFalse();
    }
}
