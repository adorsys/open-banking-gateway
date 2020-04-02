package de.adorsys.opba.fintech.impl.database;

import de.adorsys.opba.fintech.impl.config.EnableFinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EnableFinTechImplConfig
@SpringBootTest
public class RedirectUrlDatabaseTest {

    @Autowired
    protected RedirectUrlRepository redirectUrlRepository;

    @Test
    public void testSimpleSearch() {
        redirectUrlRepository.save(createEntity("peter"));
        redirectUrlRepository.save(createEntity("maksym"));

        redirectUrlRepository.findAll().forEach(en -> log.info(en.toString()));

        assertTrue(redirectUrlRepository.findByRedirectCode("maksym").isPresent());
        assertFalse(redirectUrlRepository.findByRedirectCode("maksim").isPresent());
    }

    RedirectUrlsEntity createEntity(String redirectCode) {
        RedirectUrlsEntity entity = new RedirectUrlsEntity();
        entity.setRedirectCode(redirectCode);
        entity.setNokStatePath("nok");
        entity.setOkStatePath("ok");
        return entity;
    }

}
