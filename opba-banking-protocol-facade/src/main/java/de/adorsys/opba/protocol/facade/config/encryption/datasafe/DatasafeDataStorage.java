package de.adorsys.opba.protocol.facade.config.encryption.datasafe;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Base class for Datasafe protected files storage in DB (encrypted data).
 * @param <T> Target entity that contains data
 */
@RequiredArgsConstructor
public abstract class DatasafeDataStorage<T> implements BaseDatasafeDbStorageService.StorageActions {
    protected final CrudRepository<T, UUID> repository;
    private final Function<String, T> factory;
    private final Function<String, Optional<T>> find;
    private final Function<T, byte[]> getData;
    private final BiConsumer<T, byte[]> setData;
    private final TransactionOperations txOper;

    /**
     * Updates encrypted database entry
     * @param path Path to the entry
     * @param data New entry value
     */
    @Override
    public void update(String path, byte[] data) {
        txOper.execute(callback -> {
            Optional<T> entry = find.apply(path);
            if (entry.isPresent()) {
                T toSave = entry.get();
                setData.accept(toSave, data);
                return null;
            }

            T newEntry = factory.apply(path);
            setData.accept(newEntry, data);
            repository.save(newEntry);
            return null;
        });
    }

    /**
     * Reads encrypted database entry
     * @param path Path to the entry
     */
    @Override
    public Optional<byte[]> read(String path) {
        return txOper.execute(callback -> find.apply(path).map(getData));
    }

    /**
     * Removes encrypted database entry
     * @param path Path to the entry
     */
    @Override
    public void delete(String path) {
        throw new IllegalStateException("Not allowed");
    }
}
