package de.adorsys.opba.protocol.xs2a.tests.e2e.stages;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import de.adorsys.opba.db.domain.Approach;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@JGivenStage
@SuppressWarnings("checkstyle:MethodName") // Jgiven prettifies snake-case names not camelCase
public class CommonGivenStages<SELF extends CommonGivenStages<SELF>> extends Stage<SELF> {

    @Autowired
    private BankProfileJpaRepository profiles;

    @Transactional
    public SELF preferred_sca_approach_selected_for_all_banks_in_opba(Approach approach) {
        profiles.findAll().stream()
            .map(it -> {
                it.setPreferredApproach(approach);
                return it;
            })
            .forEach(profiles::save);

        return self();
    }
}
