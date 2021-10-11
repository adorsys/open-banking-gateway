package de.adorsys.opba.helpers.protocol.testing.service;

import de.adorsys.xs2a.adapter.api.AspspReadOnlyRepository;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MapBasedAspspRepository implements AspspReadOnlyRepository {

    private final Map<UUID, Aspsp> aspspById = new ConcurrentHashMap<>();

    public void setAspsp(UUID uuid, Aspsp aspsp) {
        aspspById.put(uuid, aspsp);
    }

    @Override
    public Optional<Aspsp> findById(String id) {
        return Optional.ofNullable(aspspById.get(UUID.fromString(id)));
    }

    @Override
    public List<Aspsp> findByBic(String s, String s1, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findByBankCode(String s, String s1, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findByName(String s, String s1, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findAll(String s, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findLike(Aspsp aspsp, String s, int i) {
        return null;
    }

    @Override
    public List<Aspsp> findByIban(String s, String s1, int i) {
        return null;
    }
}
