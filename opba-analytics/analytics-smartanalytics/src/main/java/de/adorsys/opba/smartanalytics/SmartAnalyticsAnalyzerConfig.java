package de.adorsys.opba.smartanalytics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Slf4j
@Configuration
@ComponentScan(
        basePackages = {
                "de.adorsys.opba.smartanalytics",
                "de.adorsys.smartanalytics"
        },
        // Disabling SmartAnalytics HATEOAS due to missing issues
        excludeFilters = {@ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"de\\.adorsys\\.smartanalytics\\.config\\..*"}
        ), @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"de\\.adorsys\\.smartanalytics\\.exception\\..*"}
        ), @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"de\\.adorsys\\.smartanalytics\\.web\\.ServerInfoController.*"}
        ), @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"de\\.adorsys\\.smartanalytics\\.web\\..*"}
        )
        }
)
public class SmartAnalyticsAnalyzerConfig {
}

