package de.adorsys.opba.protocol.xs2a.testsandbox.internal;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class StarterContext implements AutoCloseable {

    private final Map<SandboxApp, Integer> dockerPorts = new ConcurrentHashMap<>();
    private final AtomicReference<Integer> dbPort = new AtomicReference<>();
    private final Map<SandboxApp, ClassLoader> loader = new ConcurrentHashMap<>();
    private final Map<SandboxApp, GenericContainer> dockerContainer = new ConcurrentHashMap<>();

    @Override
    public void close() {
        this.dockerContainer.forEach((key, container) -> container.stop());
    }
}
