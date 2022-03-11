package org.example.at.webdriver;

import io.qameta.atlas.core.Atlas;
import io.qameta.atlas.core.internal.Configuration;
import io.qameta.atlas.webdriver.context.WebDriverContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

public class AtlasUtils {

    private AtlasUtils() {
    }

    public static Configuration getConfiguration(Atlas atlas) {
        try {
            Field configuration = Atlas.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            return (Configuration) configuration.get(atlas);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Couldn't get Configuration", e);
        }
    }

    public static WebDriver getWebDriver(Atlas atlas) {
        return getConfiguration(atlas)
                .requireContext(WebDriverContext.class)
                .getValue();
    }

}
