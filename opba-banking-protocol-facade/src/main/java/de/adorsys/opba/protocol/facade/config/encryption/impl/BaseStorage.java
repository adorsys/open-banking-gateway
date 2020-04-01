package de.adorsys.opba.protocol.facade.config.encryption.impl;

import de.adorsys.datasafe.storage.api.StorageService;
import de.adorsys.datasafe.types.api.callback.ResourceWriteCallback;
import de.adorsys.datasafe.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe.types.api.resource.ResolvedResource;
import de.adorsys.datasafe.types.api.resource.WithCallback;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class BaseStorage implements StorageService {

    private final Map<String, StorageActions> handlers;

    @Override
    public boolean objectExists(AbsoluteLocation absoluteLocation) {
        return handlers.get(deduceTable(absoluteLocation))
                .getRead()
                .apply(deduceId(absoluteLocation))
                .isPresent();
    }

    @Override
    public Stream<AbsoluteLocation<ResolvedResource>> list(AbsoluteLocation absoluteLocation) {
        throw new IllegalStateException("Unsupported operation");
    }

    @Override
    @SneakyThrows
    public InputStream read(AbsoluteLocation absoluteLocation) {
        return new ByteArrayInputStream(requireBytes(absoluteLocation));
    }

    @Override
    public void remove(AbsoluteLocation absoluteLocation) {
        handlers.get(deduceTable(absoluteLocation)).getDelete().accept(deduceId(absoluteLocation));
    }

    @Override
    @SneakyThrows
    public OutputStream write(WithCallback<AbsoluteLocation, ? extends ResourceWriteCallback> withCallback) {
        return new SetAndSaveOnClose(
                deduceId(withCallback.getWrapped()),
                handlers.get(deduceTable(withCallback.getWrapped())).getUpdate()
        );
    }

    protected String deduceTable(AbsoluteLocation<?> path) {
        return path.location().getWrapped().getHost();
    }

    protected String deduceId(AbsoluteLocation<?> path) {
        return path.location().getWrapped().getPath().replaceAll("^/", "");
    }

    private byte[] requireBytes(AbsoluteLocation<?> location) {
        return handlers.get(deduceTable(location))
                .getRead()
                .apply(deduceId(location))
                .orElseThrow(() -> new IllegalArgumentException("Failed to find entity for " + location.location().toASCIIString()));
    }

    public interface StorageActions {
        BiConsumer<String, byte[]> getUpdate();
        Function<String, Optional<byte[]>> getRead();
        Consumer<String> getDelete();
    }

    @RequiredArgsConstructor
    private static class SetAndSaveOnClose extends OutputStream {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        private final String id;
        private final BiConsumer<String, byte[]> update;

        @Override
        public void write(int b) {
            os.write(b);
        }

        @Override
        public void write(@NotNull byte[] b) throws IOException {
            os.write(b);
        }

        @Override
        public void write(@NotNull byte[] b, int off, int len) {
            os.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            os.flush();
        }

        @Override
        public void close() throws IOException {
            os.close();
            update.accept(id, os.toByteArray());
        }
    }
}
