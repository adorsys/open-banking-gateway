package de.adorsys.opba.core.protocol.repository;

import de.adorsys.opba.core.protocol.config.hardcoded.AspspHardcodedRecord;
import de.adorsys.xs2a.adapter.service.AspspSearchService;
import de.adorsys.xs2a.adapter.service.model.Aspsp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// TODO - move this to bank configuration (settings like xs2a url) and bank profile (name, bic, etc)
@Service
@RequiredArgsConstructor
public class HardcodedAspspRepository implements AspspSearchService {

    private final AspspHardcodedRecord record;

    @Override
    public Optional<Aspsp> findById(String s) {
        if (record.getId().equals(s)) {
            return Optional.of(record.aspsp());
        }
        return Optional.empty();
    }

    @Override
    public List<Aspsp> findByBic(String s, String s1, int i) {
        if (record.getBic().equals(s)) {
            return Collections.singletonList(record.aspsp());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Aspsp> findByBankCode(String s, String s1, int i) {
        if (record.getBankCode().equals(s)) {
            return Collections.singletonList(record.aspsp());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Aspsp> findByName(String s, String s1, int i) {
        if (record.getAspspName().equals(s)) {
            return Collections.singletonList(record.aspsp());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Aspsp> findAll(String s, int i) {
        return Collections.singletonList(record.aspsp());
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
