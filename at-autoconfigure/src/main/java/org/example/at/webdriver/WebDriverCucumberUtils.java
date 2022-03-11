package org.example.at.webdriver;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

public class WebDriverCucumberUtils {

    private static final Logger log = LoggerFactory.getLogger(WebDriverCucumberUtils.class);

    private WebDriverCucumberUtils() {
    }

    public static void attachScreenshot(Scenario scenario, WebDriver webDriver) {
        if (webDriver instanceof TakesScreenshot) {
            scenario.attach(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES), "image/png", System.currentTimeMillis() + ".png");
        }
    }

    public static void attachBrowserLog(Scenario scenario, WebDriver webDriver) {
        try {
            var browserLog = webDriver.manage().logs().get(LogType.BROWSER).getAll().stream()
                    .map(it -> String.format("%s\t%s\t%s", LocalDateTime.ofInstant(Instant.ofEpochMilli(it.getTimestamp()), ZoneId.systemDefault()), it.getLevel(), it.getMessage()))
                    .collect(Collectors.joining("\n"));

            log.info("Browser log:\n{}", browserLog);

            scenario.attach(browserLog.getBytes(), "text/plain", System.currentTimeMillis() + ".txt");
        } catch (Exception e) {
            log.warn("Couldn't retrieve logs", e);
        }
    }

}
