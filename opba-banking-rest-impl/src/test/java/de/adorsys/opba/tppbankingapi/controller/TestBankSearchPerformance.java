package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.BaseMockitoTest;
import de.adorsys.opba.tppbankingapi.dto.TestResult;
import de.adorsys.opba.tppbankingapi.services.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.adorsys.opba.tppbankingapi.TestProfiles.ONE_TIME_POSTGRES_ON_DISK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@ActiveProfiles(ONE_TIME_POSTGRES_ON_DISK)
@AutoConfigureMockMvc
class TestBankSearchPerformance extends BaseMockitoTest {

    private static final int N_THREADS = Integer.parseInt(System.getProperty("SEARCH_PERF_N_THREADS", "1"));
    private static final int ITERATIONS = Integer.parseInt(System.getProperty("SEARCH_PERF_ITERATIONS", "3"));

    private final CountDownLatch latch = new CountDownLatch(ITERATIONS);
    private final AtomicInteger counter = new AtomicInteger();

    private static List<String> searchStrings = generateTestData();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBankSearch() throws Exception {
        StatisticService statisticService = new StatisticService();
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);

        log.info("Starting test endpoints...");
        Runnable runnable = getBanksEndpoint(statisticService, searchStrings);
        searchStrings.forEach(s -> executorService.execute(runnable));

        latch.await();
        printResults(statisticService);
    }

    private void printResults(StatisticService statisticService) {
        log.info("Test results:");
        statisticService.getTestResult().forEach(tr -> log.info("{}", tr));

        Long start = statisticService.getTestResult()
                .stream().map(TestResult::getStart).collect(Collectors.toList())
                .stream().min(Long::compareTo).get();
        Long end = statisticService.getTestResult()
                .stream().map(TestResult::getEnd).collect(Collectors.toList())
                .stream().max(Long::compareTo).get();
        log.info("start: {}", start);
        log.info("end: {}", end);
        log.info("{} calls completed in {} milliseconds", ITERATIONS, end - start);
        log.info("Operations per second: {}", ITERATIONS / Double.max((end - start) / 1000.0, 1e-3));
    }

    @NotNull
    private Runnable getBanksEndpoint(StatisticService statisticService, List<String> searchStrings) {
        return () -> {
            int cnt = counter.incrementAndGet();
            String searchString = searchStrings.get(cnt - 1);
            long start = System.currentTimeMillis();
            try {
                MvcResult mvcResult = mockMvc.perform(
                        get("/v1/banking/search/bank-search")
                                .header("Authorization", "123")
                                .header("X-Request-ID", "3ab706f2-8cc8-462e-8393-a43f6ee87e53")
                                .header("Compute-PSU-IP-Address", "true")
                                .header("X-Timestamp-UTC", "2020-04-17T13:45:17.069Z")
                                .header("X-Request-Signature", "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJmaW50ZWNoQGF3ZXNvbWUtZmludGVjaC5jb20iLCJpc3MiOiJmaW50ZWNoLmNvbSIsInNpZ24tZGF0YSI6IjNhYjcwNmYyLThjYzgtNDYyZS04MzkzLWE0M2Y2ZWU4N2U1MzIwMjAtMDQtMTdUMTM6NDU6MTcuMDY5WiJ9.S3L4XdAhlzJBXYHTXMXVNlLmABBkUvYqF03znEmzKQU9vOF-n0cT6yWWjvm6T82ISzZ5OYrJaA2QJekFsw78vraY-t7vxhWVn9hO_C1tJR_rV3SFWi6mtZeuSCGDSJxEB_8gmMqFomQs0sEdBayiC1mkW9R3TQGhmLkXyM4GHGR_rHL1oLFjG3Ueo0tYmLVIJDyQ6oqFHhDdNro41O2E1S9BOOVLbANLU7r_jN8KIuujmFIBF3S7L0P2yvIHQ3Sme3W2550m-LdPI3f2SFD4ZRLG6Xsc8LyrDuXtEuk9H3nHqPenbhQnMPHK7OUcsEN2VFqvUQ9SWTgUz4P9nuU2ng")
                                .header("Fintech-ID", "MY-SUPER-FINTECH-ID")
                                .param("keyword", searchString)
                                .param("max", "10")
                                .param("start", "0"))
                        .andExpect(status().isOk())
                        .andReturn();
                long end = System.currentTimeMillis();
                TestResult testResult = new TestResult(start, end, searchString, mvcResult.getResponse().getContentAsString());
                statisticService.getTestResult().add(testResult);
                latch.countDown();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        };
    }

    @NotNull
    private static List<String> generateTestData() {
        log.info("Generating test data...");
        List<String> searchStrings = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            int charCount = new Random().nextInt(5) + 3; // [3..7]
            searchStrings.add(RandomStringUtils.randomAlphabetic(charCount));
        }
        return searchStrings;
    }

}
