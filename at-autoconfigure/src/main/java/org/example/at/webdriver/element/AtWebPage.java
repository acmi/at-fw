package org.example.at.webdriver.element;

import io.qameta.atlas.webdriver.WebPage;
import io.qameta.atlas.webdriver.extension.DriverProvider;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

public interface AtWebPage extends WrapsDriver, SearchContext, WebPage {
    @DriverProvider
    WebDriver getWrappedDriver();

    @Override
    @Deprecated
    void open(String url);

    @Override
    @Deprecated
    void open();
}
