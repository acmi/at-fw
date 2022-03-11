package org.example.at.autoconfigure.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DelegateRemoteWebDriver implements AtWebDriver {

    private RemoteWebDriver webDriver;

    public DelegateRemoteWebDriver(RemoteWebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public DelegateRemoteWebDriver() {
    }

    public void setDelegate(RemoteWebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public void get(String s) {
        webDriver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return webDriver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return webDriver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return webDriver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return webDriver.getPageSource();
    }

    @Override
    public void close() {
        webDriver.close();
    }

    @Override
    public void quit() {
        webDriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return webDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return webDriver.navigate();
    }

    @Override
    public Options manage() {
        return webDriver.manage();
    }

    @Override
    public Object executeScript(String s, Object... objects) {
        return webDriver.executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return webDriver.executeAsyncScript(s, objects);
    }

    @Override
    public Capabilities getCapabilities() {
        return webDriver.getCapabilities();
    }

    @Override
    public void perform(Collection<Sequence> collection) {
        webDriver.perform(collection);
    }

    @Override
    public void resetInputState() {
        webDriver.resetInputState();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) {
        return webDriver.getScreenshotAs(outputType);
    }

    @Override
    public String toString() {
        return "DelegateRemoteWebDriver(" + webDriver + ")";
    }

}
