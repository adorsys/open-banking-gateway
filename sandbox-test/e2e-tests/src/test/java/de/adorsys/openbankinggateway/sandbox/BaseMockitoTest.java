package de.adorsys.openbankinggateway.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class BaseMockitoTest {

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void validate() {
        Mockito.validateMockitoUsage();
    }
}
