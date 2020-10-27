package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.dto.AnalyzeableTransaction;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategorizer {

    private final KieContainer kContainer;

    public TransactionCategorizer(@Value("${drools.categorizer-ruleset}") String transactionCategorizerRuleset) {
        KieServices services = KieServices.Factory.get();
        KieFileSystem fileSystem = services.newKieFileSystem();

        // rules to use when generating random actions:
        fileSystem.write(ResourceFactory.newClassPathResource(transactionCategorizerRuleset));

        KieBuilder kb = services.newKieBuilder(fileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();

        this.kContainer = services.newKieContainer(kieModule.getReleaseId());
    }

    public String categorizeTransaction(TransactionDetails txn) {
        StatelessKieSession session = kContainer.newStatelessKieSession();
        AnalyzeableTransaction toAnalyze = new AnalyzeableTransaction(txn);
        session.execute(toAnalyze);

        if (null == toAnalyze.getCategory()) {
            return null;
        }

        if (null == toAnalyze.getSpecification()) {
            return String.format("%s %s", toAnalyze.getCategory(), toAnalyze.getSubCategory());
        }

        return toAnalyze.getSpecification();
    }
}
