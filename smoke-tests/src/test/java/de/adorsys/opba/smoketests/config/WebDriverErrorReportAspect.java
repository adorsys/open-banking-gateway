package de.adorsys.opba.smoketests.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a helper aspect that allows logging of WebDriver url's and screens for easier debugging.
 */
@Slf4j
@Aspect
public class WebDriverErrorReportAspect {

    private static final Map<String, DriverInfo> DRIVERS = new ConcurrentHashMap<>();

    @Around(value = "execution(* *(.., (org.openqa.selenium.WebDriver+), ..)) && (@annotation(org.junit.jupiter.api.Test) || @within(org.junit.jupiter.api.Test))")
    public Object runTestMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        WebDriver driver = (WebDriver) Arrays.stream(joinPoint.getArgs())
                .filter(it -> it instanceof WebDriver)
                .findFirst()
                .get();

        String threadId = UUID.randomUUID().toString();
        DRIVERS.computeIfAbsent(threadId, id -> new DriverInfo(driver));
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(threadId);
        try {
            return joinPoint.proceed();
        }
        catch (Throwable ex) {
            DriverInfo info = DRIVERS.get(threadId);
            log.error("Failed due to {}", ex.getMessage());
            log.error("Last WebDriver sequence:");
            info.getLogs().forEach(it -> log.error("{}", it));
            throw ex;
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    @Around(value = "execution(* de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation.*(.., (org.openqa.selenium.WebDriver+), ..))")
    public Object runTestStep(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        try {
            readAndStoreWebDriverData(methodName, true);
            return joinPoint.proceed();
        } finally {
            readAndStoreWebDriverData(methodName, false);
        }
    }

    private void readAndStoreWebDriverData(String methodName, boolean isBefore) {
        String id = Thread.currentThread().getName();
        if (!DRIVERS.containsKey(id)) {
            return;
        }

        DriverInfo info = DRIVERS.get(id);
        WebDriver driver = info.getDriver();
        WebDriverInfoEntry entry = new WebDriverInfoEntry(
                methodName,
                isBefore,
                driver.getCurrentUrl(),
                driver instanceof TakesScreenshot ? ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64) : null
        );

        info.getLogs().push(new WebDriverLog(entry));
    }

    @Data
    private static class DriverInfo {

        private final WebDriver driver;
        private final Stack<WebDriverLog> logs = new Stack<>();
    }

    @Data
    private static class WebDriverLog {
        private final WebDriverInfoEntry entry;

        @Override
        public String toString() {
            return String.format("[%s:%s]%nurl: %s%nscreenshot:%n%s%n",
                    entry.isBefore() ? "BEFORE" : "AFTER",
                    entry.getMethodName(),
                    entry.getUrl(),
                    entry.getScreenshot()
            );
        }
    }

    @Data
    private static class WebDriverInfoEntry {

        private final String methodName;
        private final boolean isBefore;
        private final String url;
        private final String screenshot;
    }
}
