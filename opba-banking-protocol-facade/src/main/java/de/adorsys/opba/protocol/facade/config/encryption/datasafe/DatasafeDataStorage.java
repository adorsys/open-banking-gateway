package de.adorsys.opba.protocol.facade.config.encryption.datasafe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class DatasafeDataStorage<T> implements BaseDatasafeDbStorageService.StorageActions {
    protected final CrudRepository<T, UUID> repository;
    private final BiFunction<Long, UUID, T> factory;
    private final Function<T, byte[]> getData;
    private final BiConsumer<T, byte[]> setData;

    @Override
    @Transactional
    public void update(String id, byte[] data) {
        Optional<T> entry = find(id);
        if (entry.isPresent()) {
            T toSave = entry.get();
            setData.accept(toSave, data);
            return;
        }

        T newEntry = factory.apply(parentId(id), uuid(id));
        setData.accept(newEntry, data);
        repository.save(newEntry);
    }

    @Override
    @Transactional
    public Optional<byte[]> read(String id) {
        return find(id).map(getData);
    }

    @Override
    @Transactional
    public void delete(String id) {
        throw new IllegalStateException("Not allowed");
    }

    protected UUID uuid(String compositeId) {
        return UUID.fromString(compositeId.split("/")[1]);
    }

    protected Long parentId(String compositeId) {
        return Long.parseLong(compositeId.split("/")[0]);
    }

    protected Optional<T> find(String id) {
        return repository.findById(uuid(id));
    }
}
