package de.adorsys.opba.protocol.bpmnshared.config.flowable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class LargeJsonCustomSerializerTest {
    private static LargeJsonCustomSerializer serializer;

    @BeforeAll
    public static void init() {
        serializer = new LargeJsonCustomSerializer(null, new ObjectMapper(), Arrays.asList("java.lang.String"), 2048);
    }

    @Test
    void serializeLessThanMinValueTest() {
        String input = generateRandomStringOfLength(2046);
        assertThat(serializer.isAbleToStore(input)).isFalse();
    }

    @Test
    void serializeMoreToMinValueTest() {
        String input = generateRandomStringOfLength(2047);
        assertThat(serializer.isAbleToStore(input)).isTrue();
    }

    private String generateRandomStringOfLength(int length) {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}
