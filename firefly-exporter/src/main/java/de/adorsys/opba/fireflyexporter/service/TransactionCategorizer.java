package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.dto.AnalyzeableTransaction;
import de.adorsys.opba.tpp.ais.api.model.generated.TransactionDetails;
import lombok.Data;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class TransactionCategorizer {

    private final KieContainer kContainer;

    public TransactionCategorizer(CategorizerRuleset categorizerRules) {
        KieServices services = KieServices.Factory.get();
        KieFileSystem fileSystem = services.newKieFileSystem();

        // rules to use when generating random actions:
        categorizerRules.getRuleset().forEach(it -> fileSystem.write(ResourceFactory.newClassPathResource(it)));

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

    @Data
    @Validated
    @Configuration
    @ConfigurationProperties("drools.categorizer")
    public static class CategorizerRuleset {

        @Valid
        @NotEmpty
        private List<@NotNull String> ruleset;
    }
}
