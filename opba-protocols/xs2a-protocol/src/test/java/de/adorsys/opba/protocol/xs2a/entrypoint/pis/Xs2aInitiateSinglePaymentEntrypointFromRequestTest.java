package de.adorsys.opba.protocol.xs2a.entrypoint.pis;

import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.xs2a.config.MapperTestConfig;
import de.adorsys.opba.protocol.xs2a.context.pis.SinglePaymentXs2aContext;
import de.adorsys.opba.protocol.xs2a.util.FixtureProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(classes = MapperTestConfig.class)
class Xs2aInitiateSinglePaymentEntrypointFromRequestTest {

    public static final String REQUEST_INPUT = "mapper-test-fixtures/payment_initiation_xs2a_context_from_initiated_single_payment_request_input.json";
    public static final String REQUEST_OUTPUT = "mapper-test-fixtures/payment_initiation_xs2a_context_from_initiated_single_payment_request_output.json";

    @Autowired
    private Xs2aInitiateSinglePaymentEntrypoint.FromRequest mapper;

    @Autowired
    private FixtureProvider fixtureProvider;

    @Test
    @SneakyThrows
    public void xs2aInitiateSinglePaymentEntrypointFromRequestMapperTest() {
        // Given
        InitiateSinglePaymentRequest mappingInput = fixtureProvider.getFromFile(REQUEST_INPUT, InitiateSinglePaymentRequest.class);
        SinglePaymentXs2aContext expected = fixtureProvider.getFromFile(REQUEST_OUTPUT, SinglePaymentXs2aContext.class);
        // When
        SinglePaymentXs2aContext actual = mapper.map(mappingInput);
        // Then
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }
}