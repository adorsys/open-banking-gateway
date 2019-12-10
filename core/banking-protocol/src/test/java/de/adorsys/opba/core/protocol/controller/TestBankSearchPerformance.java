package de.adorsys.opba.core.protocol.controller;

import de.adorsys.opba.core.protocol.BaseMockitoTest;
import de.adorsys.opba.core.protocol.dto.TestResult;
import de.adorsys.opba.core.protocol.services.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@Slf4j
@Testcontainers
public class TestBankSearchPerformance extends BaseMockitoTest {

    private static final int POSTGRES_PORT = 5432;
    private static final int N_THREADS = 10;
    private static final int ITERATIONS = N_THREADS * 100;

    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicInteger counterFTS = new AtomicInteger();

    private MockMvc mockMvc;

    @Container
    @SuppressWarnings("PMD.UnusedPrivateField")
    private static final DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("postgres", POSTGRES_PORT)
                    .waitingFor("postgres", Wait.forListeningPort());

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void onSetUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void TestBankSearch() throws Exception {
        StatisticService statisticService = new StatisticService();
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);

        List<String> searchStrings = generateTestData();

        log.info("Starting test endpoints...");
        Runnable runnable = getBanksEnpoint(statisticService, searchStrings);
        searchStrings.forEach(s -> executorService.execute(runnable));

        executorService.awaitTermination(60L, TimeUnit.SECONDS);
        printResults(statisticService);

        statisticService = new StatisticService();
        log.info("Starting test FTS endpoints...");
        Runnable runnableFTS = getBanksFTSEnpoint(statisticService, searchStrings);
        searchStrings.forEach(s -> executorService.execute(runnableFTS));

        executorService.awaitTermination(60L, TimeUnit.SECONDS);
        printResults(statisticService);
    }

    private void printResults(StatisticService statisticService) {
        log.info("Test results:");
        statisticService.getTestResult().forEach(System.out::println);

        Long start = statisticService.getTestResult()
                .stream().map(TestResult::getStart).collect(Collectors.toList())
                .stream().min(Long::compareTo).get();
        Long end = statisticService.getTestResult()
                .stream().map(TestResult::getEnd).collect(Collectors.toList())
                .stream().max(Long::compareTo).get();
        log.info("{} calls completed in {} milliseconds", ITERATIONS, end - start);
    }

    @NotNull
    private Runnable getBanksEnpoint(StatisticService statisticService, List<String> searchStrings) {
        return () -> {
            int cnt = counter.incrementAndGet();
            String searchString = searchStrings.get(cnt - 1);
            long start = System.currentTimeMillis();
            try {
                MvcResult mvcResult = mockMvc.perform(
                        get("/v1/banks")
                                .param("q", searchString)
                                .param("max_results", "10"))
                        .andExpect(status().isOk())
                        .andReturn();
                long end = System.currentTimeMillis();
                TestResult testResult = new TestResult(start, end, searchString, mvcResult.getResponse().getContentAsString());
                statisticService.getTestResult().add(testResult);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
    }

    @NotNull
    private Runnable getBanksFTSEnpoint(StatisticService statisticService, List<String> searchStrings) {
        return () -> {
            int cnt = counterFTS.incrementAndGet();
            String searchString = searchStrings.get(cnt - 1);
            long start = System.currentTimeMillis();
            try {
                MvcResult mvcResult = mockMvc.perform(
                        get("/v1/banks/fts")
                                .param("q", "deu")
                                .param("max_results", "10"))
                        .andExpect(status().isOk())
                        .andReturn();
                long end = System.currentTimeMillis();
                TestResult testResult = new TestResult(start, end, searchString, mvcResult.getResponse().getContentAsString());
                statisticService.getTestResult().add(testResult);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
    }

    @NotNull
    private List<String> generateTestData() {
        log.info("Generating test data...");
        List<String> searchStrings = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            int charCount = new Random().nextInt(5) + 3; // [3..7]
            searchStrings.add(RandomStringUtils.randomAlphabetic(charCount));
        }
        return searchStrings;
    }

}
