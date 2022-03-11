package org.example.at.webdriver.hooks;

import org.example.at.autoconfigure.webdriver.Scenario;

public class CucumberScenario implements Scenario {

    private final io.cucumber.java.Scenario scenario;

    public CucumberScenario(io.cucumber.java.Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public String getName() {
        return scenario.getName();
    }

    @Override
    public String getId() {
        return scenario.getId();
    }

    @Override
    public boolean isFailed() {
        return scenario.isFailed();
    }

    @Override
    public void attach(byte[] data, String mimeType, String name) {
        scenario.attach(data, mimeType, name);
    }

}
