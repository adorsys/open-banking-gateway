package de.adorsys.opba.protocol.api.fintechspec;

import java.util.Map;

public interface ApiConsumerConfig {
    Map<String, ? extends ApiConsumer> getConsumers();
}
