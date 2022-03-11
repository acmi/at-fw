package org.example.at.webdriver.steps;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.example.at.utils.DynamicContext;
import org.openqa.selenium.WebDriver;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.example.at.utils.Eval.eval;
import static org.example.at.webdriver.CurrentUrlMatcher.currentUrl;
import static org.example.at.webdriver.WebDriverCucumberUtils.attachScreenshot;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.should;
import static ru.yandex.qatools.matchers.decorators.MatcherDecorators.timeoutHasExpired;

public class WebDriverSteps {

    private final DynamicContext context;
    private final WebDriver webDriver;
    private Scenario scenario;

    public WebDriverSteps(DynamicContext context, WebDriver webDriver) {
        this.context = context;
        this.webDriver = webDriver;
    }

    @Before
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Когда("открыта страница {}")
    public void открыта_страница(String url) {
        webDriver.get(url);
    }

    @Тогда("проверить, что осуществлен переход на страницу {}")
    public void проверить__что_осуществлен_переход_на_страницу(String urlPart) {
        assertThat(webDriver, should(currentUrl(containsString((String) eval(urlPart, context)))).whileWaitingUntil(timeoutHasExpired(MINUTES.toMillis(3))));
    }

    @Тогда("сделать скриншот")
    public void сделать_скриншот() {
        attachScreenshot(scenario, webDriver);
    }

}
