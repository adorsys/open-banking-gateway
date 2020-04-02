package de.adorsys.opba.protocol.facade.config.encryption.datasafe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class DatasafeMetadataStorage<T> implements BaseDatasafeDbStorageService.StorageActions {

    private final CrudRepository<T, Long> repository;
    private final Function<T, byte[]> getData;
    private final BiConsumer<T, byte[]> setData;

    @Override
    @Transactional
    public void update(String id, byte[] data) {
        T toSave = repository.findById(getIdValue(id)).get();
        setData.accept(toSave, data);
        repository.save(toSave);
    }

    @Override
    @Transactional
    public Optional<byte[]> read(String id) {
        return repository.findById(Long.valueOf(id)).map(getData);
    }

    @Override
    @Transactional
    public void delete(String id) {
        throw new IllegalStateException("Not allowed");
    }

    protected Long getIdValue(String id) {
        return Long.valueOf(id);
    }
}
