package org.example.at.webdriver.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.example.at.autoconfigure.webdriver.AtWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.at.webdriver.WebDriverCucumberUtils.attachBrowserLog;
import static org.example.at.webdriver.WebDriverCucumberUtils.attachScreenshot;

public class WebDriverSetup {

    private static final Logger log = LoggerFactory.getLogger(WebDriverSetup.class);

    private static final int MAX_RESIZE_ATTEMPTS = 3;

    private final AtWebDriver webDriver;

    public WebDriverSetup(AtWebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Before
    public void before(Scenario scenario) {
        for (int i = MAX_RESIZE_ATTEMPTS; i > 0; ) {
            i = i--;

            webDriver.beforeTest(new CucumberScenario(scenario));

            try {
                webDriver.manage().window().maximize();
                break;
            } catch (Exception e) {
                log.warn("Browser hasn't started correctly, {} attempts remain", i, e);

                webDriver.afterTest(new CucumberScenario(scenario));
            }
        }
    }

    @After
    public void after(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot(scenario, webDriver);
            attachBrowserLog(scenario, webDriver);
        }

        webDriver.afterTest(new CucumberScenario(scenario));
    }

}
