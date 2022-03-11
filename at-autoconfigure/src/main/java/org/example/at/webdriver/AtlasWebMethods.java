package org.example.at.webdriver;

import io.qameta.atlas.core.Atlas;
import org.example.at.webdriver.element.AtWebPage;
import org.openqa.selenium.WebDriver;

public interface AtlasWebMethods {

    Atlas getAtlas();

    default WebDriver getWebDriver() {
        return AtlasUtils.getWebDriver(getAtlas());
    }

    default <T extends AtWebPage> T page(Class<T> pageClass) {
        return getAtlas().create(getWebDriver(), pageClass);
    }

}
