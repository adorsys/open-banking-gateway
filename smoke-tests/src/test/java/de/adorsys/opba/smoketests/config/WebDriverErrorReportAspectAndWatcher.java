package de.adorsys.opba.smoketests.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a helper aspect that allows logging of WebDriver url's and screens for easier debugging.
 * Using wacky aspects because of this issue:
 * https://github.com/junit-team/junit5/issues/1139
 * We can't access parameter instances, even using reflection.
 */
@Slf4j
@Aspect
public class WebDriverErrorReportAspectAndWatcher implements TestWatcher {

    private static final int SCREENS_TO_LOG = 3;

    private static final Map<String, DriverInfo> DRIVERS = new ConcurrentHashMap<>();

    @Around(value = "execution(* *(.., (org.openqa.selenium.WebDriver+), ..)) && (@annotation(org.junit.jupiter.api.Test) || @within(org.junit.jupiter.api.Test))")
    public Object runTestMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        WebDriver driver = (WebDriver) Arrays.stream(joinPoint.getArgs())
                .filter(it -> it instanceof WebDriver)
                .findFirst()
                .get();

        String threadId = joinPoint.getSignature().getName();
        DRIVERS.computeIfAbsent(threadId, id -> new DriverInfo(driver));
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(threadId);
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            logWebDriverHistoryForFailure(threadId, ex);
            throw ex;
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    @Around(value = "execution(public * de.adorsys.opba.protocol.xs2a.tests.e2e.sandbox.servers.WebDriverBasedAccountInformation.*(.., (org.openqa.selenium.WebDriver+), ..))")
    public Object runTestStep(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        try {
            logIfNeeded(joinPoint, methodName, true);
            return joinPoint.proceed();
        } finally {
            logIfNeeded(joinPoint, methodName, false);
        }
    }

    private void logIfNeeded(ProceedingJoinPoint joinPoint, String methodName, boolean b) {
        // JGiven wraps class into ByteBuddy, so Aspect is triggered twice, but joinPoint once
        // we need only real calls - with known source file location.
        if (!joinPoint.getSourceLocation().getFileName().toLowerCase().contains("unknown")) {
            readAndStoreWebDriverData(methodName, b);
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (!context.getTestMethod().isPresent()) {
            return;
        }

        logWebDriverHistoryForFailure(context.getTestMethod().get().getName(), cause);
    }

    private void logWebDriverHistoryForFailure(String driverId, Throwable ex) {
        DriverInfo info = DRIVERS.get(driverId);
        if (null == info) {
            return;
        }

        log.error("Failed due to {}", ex.getMessage(), ex);
        log.error("Last {} screens of WebDriver sequence:", SCREENS_TO_LOG);
        int skipScreenCount = Math.max(0, info.getLogs().size() - SCREENS_TO_LOG);
        info.getLogs().stream().skip(skipScreenCount).forEach(it -> log.error("{}", it));
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

        info.getLogs().add(new WebDriverLog(entry));
    }

    @Data
    private static class DriverInfo {

        private final WebDriver driver;
        private final List<WebDriverLog> logs = new ArrayList<>();
    }

    @Data
    private static class WebDriverLog {
        private final WebDriverInfoEntry entry;

        @Override
        public String toString() {
            return String.format("[%s:%s]%nurl: %s%nscreenshot:%n%s",
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
