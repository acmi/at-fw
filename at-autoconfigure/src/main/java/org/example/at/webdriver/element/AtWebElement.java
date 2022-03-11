package org.example.at.webdriver.element;

import io.qameta.atlas.webdriver.AtlasWebElement;
import io.qameta.atlas.webdriver.extension.DriverProvider;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.remote.RemoteWebElement;

public interface AtWebElement extends WebElement, WrapsDriver, TakesScreenshot, Locatable, AtlasWebElement<RemoteWebElement> {
    @DriverProvider
    WebDriver getWrappedDriver();
}
