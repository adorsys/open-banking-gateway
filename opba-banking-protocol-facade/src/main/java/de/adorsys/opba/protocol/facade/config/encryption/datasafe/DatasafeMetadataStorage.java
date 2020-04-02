package de.adorsys.opba.protocol.facade.config.encryption.datasafe;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.repository.jpa.fintech.FintechRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class DatasafeMetadataStorage implements BaseDatasafeDbStorageService.StorageActions {

    private final FintechRepository fintechs;
    private final Function<Fintech, byte[]> getData;
    private final BiConsumer<Fintech, byte[]> setData;

    @Override
    @Transactional
    public void update(String id, byte[] data) {
        Fintech fintech = fintechs.findById(getIdValue(id)).get();
        setData.accept(fintech, data);
        fintechs.save(fintech);
    }

    @Override
    @Transactional
    public Optional<byte[]> read(String id) {
        return fintechs.findById(Long.valueOf(id)).map(getData);
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
