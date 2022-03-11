package org.example.at.autoconfigure.webdriver;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import static org.apache.commons.io.FileUtils.toFile;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.IOUtils.copy;

public interface AtWebDriver extends WebDriver, JavascriptExecutor, HasCapabilities, Interactive, TakesScreenshot {
    default String prepareClasspathResourceToUpload(String resource) {
        var res = getClass().getResource(resource);
        if (res == null) {
            throw new RuntimeException("Resource not found: " + resource);
        }
        File file = toFile(res);
        if (file == null) {
            try {
                file = File.createTempFile("upload", getExtension(resource));
                file.deleteOnExit();
                try (InputStream is = res.openStream();
                     OutputStream os = new FileOutputStream(file)) {
                    copy(is, os);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Couldn't copy resource to temp file", e);
            }
        }
        return file.getAbsolutePath();
    }

    default void beforeTest(Scenario scenario) {
    }

    default void afterTest(Scenario scenario) {
    }
}
