package org.example.tests;

import org.example.at.autoconfigure.context.DynamicContextAutoConfiguration;
import org.example.at.autoconfigure.webdriver.WebDriverAtlasAutoConfiguration;
import org.example.at.autoconfigure.webdriver.WebDriverAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {
                DynamicContextAutoConfiguration.class,
                WebDriverAtlasAutoConfiguration.class,
                WebDriverAutoConfiguration.class,
        },
        properties = {
//                "logging.level.root=debug",
                "at.webdriver.type=testcontainers",
                "at.webdriver.browser.type=chrome",
                "at.webdriver.video.recording-policy=ALL",
        })
public class BaseTest {
}
