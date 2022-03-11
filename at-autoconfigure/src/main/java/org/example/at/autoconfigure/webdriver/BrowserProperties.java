package org.example.at.autoconfigure.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrowserProperties {

    private String type = "chrome";
    private final Map<String, Object> capability = new LinkedHashMap<>();
    private final List<String> argument = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getCapability() {
        return capability;
    }

    public List<String> getArgument() {
        return argument;
    }

    public Capabilities toCapabilities() {
        var capabilities = create();
        getCapability().forEach(capabilities::setCapability);
        return capabilities;
    }

    private MutableCapabilities create() {
        switch (getType()) {
            case "chrome":
                var chromeOptions = new ChromeOptions();
                chromeOptions.addArguments(getArgument());
                return chromeOptions;
            case "firefox":
                var firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments(getArgument());
                return firefoxOptions;
            case "edge":
                return new EdgeOptions();
            case "opera":
                var operaOptions = new OperaOptions();
                operaOptions.addArguments(getArgument());
                return operaOptions;
            case "iexplorer":
                return new InternetExplorerOptions();
            default:
                throw new UnsupportedOperationException("Browser type " + getType() + " is not supported");
        }
    }

}
