package org.example.at.autoconfigure.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.github.bonigarcia.wdm.config.WebDriverManagerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.RecordingFileFactory;
import org.testcontainers.lifecycle.TestDescription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.util.FileCopyUtils.copyToByteArray;
import static org.springframework.util.StringUtils.getFilenameExtension;

@Configuration
@ConditionalOnClass({WebDriver.class})
public class WebDriverAutoConfiguration {

    @Bean
    @ConfigurationProperties("at.webdriver.browser")
    public BrowserProperties browserProperties() {
        return new BrowserProperties();
    }

    @Lazy
    @Configuration
    @ConditionalOnMissingBean(WebDriver.class)
    @ConditionalOnProperty(name = "at.webdriver.type", havingValue = "local", matchIfMissing = true)
    public static class Local {

        private static final Log log = LogFactory.getLog(Local.class);

        private static final Map<String, Function<Capabilities, RemoteWebDriver>> DRIVER_FACTORY = Map.of(
                "chrome", ChromeDriver::new,
                "firefox", FirefoxDriver::new,
                "edge", EdgeDriver::new,
                "opera", OperaDriver::new,
                "iexplorer", InternetExplorerDriver::new
        );

        private BrowserProperties browserProperties;
        private RemoteWebDriver webDriver;

        public BrowserProperties getBrowserProperties() {
            if (browserProperties == null) {
                browserProperties = new BrowserProperties();
            }
            return browserProperties;
        }

        @Autowired
        public void setBrowserProperties(BrowserProperties browserProperties) {
            this.browserProperties = browserProperties;
        }

        @PostConstruct
        public void init() {
            String driverTypeStr = getBrowserProperties().getType();
            if (!DRIVER_FACTORY.containsKey(driverTypeStr)) {
                throw new IllegalArgumentException("Unsupported driver type: " + driverTypeStr);
            }

            DriverManagerType driverType = DriverManagerType.valueOf(driverTypeStr.toUpperCase());
            try {
                WebDriverManager.getInstance(driverType).setup();
            } catch (WebDriverManagerException e) {
                log.warn("Couldn't obtain " + driverType + "driver: " + e.getMessage());

                if (driverType == DriverManagerType.CHROME) {
                    log.info("Trying to use vsegda mirror.");

                    try {
                        System.setProperty("wdm.chromeDriverUrl", "https://artifactory.vsegda.da/vsegdada-3rdparty-local/chromedriver/index.xml");
                        System.setProperty("wdm.chromeDriverVersion", "latest");
                        System.setProperty("wdm.avoidAutoVersion", "true");

                        WebDriverManager.chromedriver().setup();
                    } catch (Exception e1) {
                        log.warn("Couldn't obtain " + driverType + "driver from vsegda mirror: " + e1);
                    }
                }
            }

            webDriver = DRIVER_FACTORY.get(driverTypeStr).apply(getBrowserProperties().toCapabilities());
        }

        @PreDestroy
        public void shutdown() {
            webDriver.quit();
            webDriver = null;
        }

        @Bean
        @Scope("cucumber-glue")
        public AtWebDriver webDriver() {
            return new DelegateRemoteWebDriver(webDriver) {
                @Override
                public void afterTest(Scenario scenario) {
                    try {
                        webDriver.manage().deleteAllCookies();
                    } catch (Exception e) {
                        log.warn("Couldn't delete cookies: " + e);
                    }
                    try {
                        webDriver.executeScript("localStorage.clear();");
                    } catch (Exception e) {
                        log.warn("Couldn't clear localStorage: " + e);
                    }
                    try {
                        webDriver.executeScript("sessionStorage.clear();");
                    } catch (Exception e) {
                        log.warn("Couldn't clear sessionStorage: " + e);
                    }
                }

                @Override
                public void close() {
                }

                @Override
                public void quit() {
                }
            };
        }

    }

    @Lazy
    @Configuration
    @ConditionalOnMissingBean(WebDriver.class)
    @ConditionalOnProperty(name = "at.webdriver.type", havingValue = "testcontainers")
    public static class Testcontainers {

        private static final Log log = LogFactory.getLog(Testcontainers.class);

        private BrowserProperties browserProperties;
        private String recordingPolicy;

        @Autowired
        public void setBrowserProperties(BrowserProperties browserProperties) {
            this.browserProperties = browserProperties;
        }

        @Value("${at.webdriver.video.recording-policy:ERRORS}")
        public void setRecordingPolicy(String recordingPolicy) {
            this.recordingPolicy = recordingPolicy;
        }

        @Bean
        @Scope("cucumber-glue")
        public AtWebDriver webDriver() {
            var capabilities = browserProperties.toCapabilities();
            if (capabilities instanceof ChromeOptions) {
                // https://stackoverflow.com/questions/48450594/selenium-timed-out-receiving-message-from-renderer
                var chromeOptions = (ChromeOptions) capabilities;
                chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
                chromeOptions.addArguments("start-maximized");
                chromeOptions.addArguments("enable-automation");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-browser-side-navigation");
                chromeOptions.addArguments("--disable-gpu");
            }
            return new ContainerWebDriver(capabilities, recordingPolicy);
        }

        public static class ContainerWebDriver extends DelegateRemoteWebDriver {
            private final Capabilities capabilities;
            private final String recordingPolicy;
            private final File recordingDirectory = new File("./target/video/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss")));
            private final RecordingFileFactory recordingFileFactory = (File vncRecordingDirectory, String prefix, boolean succeeded) -> {
                var resultMarker = succeeded ? "PASSED" : "FAILED";
                return new File(vncRecordingDirectory, resultMarker + "-" + prefix + ".flv");
            };
            private BrowserWebDriverContainer browserContainer;

            {
                recordingDirectory.mkdirs();
            }

            public ContainerWebDriver(Capabilities capabilities, String recordingPolicy) {
                this.capabilities = capabilities;
                this.recordingPolicy = recordingPolicy;
            }

            public File getRecordingDirectory() {
                return recordingDirectory;
            }

            private BrowserWebDriverContainer.VncRecordingMode getRecordingMode() {
                switch (recordingPolicy) {
                    case "ALL":
                        return BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;
                    case "ERRORS":
                        return BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;
                    default:
                        return BrowserWebDriverContainer.VncRecordingMode.SKIP;
                }
            }

            @Override
            public void beforeTest(Scenario scenario) {
                browserContainer = new BrowserWebDriverContainer()
                        .withCapabilities(capabilities)
                        .withRecordingMode(getRecordingMode(), getRecordingDirectory())
                        .withRecordingFileFactory(recordingFileFactory);

                browserContainer.start();

                browserContainer.beforeTest(new SimpleTestDescription(scenario.getId(), scenario.getName()));

                var webDriver = browserContainer.getWebDriver();
                webDriver.setFileDetector(new LocalFileDetector());
                setDelegate(webDriver);
            }

            @Override
            public void afterTest(Scenario scenario) {
                setDelegate(null);

                var description = new SimpleTestDescription(scenario.getId(), scenario.getName());
                browserContainer.afterTest(description, scenario.isFailed() ? Optional.of(new Exception("Test failed")) : Optional.empty());

                browserContainer.stop();

                boolean encode;
                switch (recordingPolicy) {
                    case "ALL":
                        encode = true;
                        break;
                    case "ERRORS":
                        encode = scenario.isFailed();
                        break;
                    case "NONE":
                    default:
                        encode = false;
                }
                try {
                    var video = prepareVideo(description.getFilesystemFriendlyName(), encode);

                    if (video != null) {
                        var ext = getFilenameExtension(video.getName());
                        var mime = "mp4".equals(ext) ? "video/mp4" : "video/x-flv";

                        scenario.attach(copyToByteArray(video), mime, video.getName());
                    }
                } catch (IOException e) {
                    log.warn("Couldn't write video attachment", e);
                }

                browserContainer = null;
            }

            private File prepareVideo(String name, boolean encode) throws IOException {
                var files = recordingDirectory.listFiles(it ->
                        it.getName().contains(name) && it.getName().endsWith("flv")
                );
                if (files == null || files.length == 0) {
                    log.warn("Couldn't find recorded video " + name);
                    return null;
                }

                return files[0];

//                var original = files[0];
//                File result;
//                FFmpegContrainer.Codec codec;
//
//                if (encode) {
//                    result = new File(original.getParent(), original.getName().substring(0, original.getName().lastIndexOf('.') + 1) + "mp4");
//                    codec = FFmpegContrainer.PredefinedCodec.MP4;
//                } else {
//                    result = original;
//                    codec = FFmpegContrainer.PredefinedCodec.SAME;
//                }
//
//                var ffmpeg = new FFmpegContrainer(MountableFile.forHostPath(original.getAbsolutePath()), codec)
//                        .withLogConsumer(OutputFrame::getUtf8String);
//                ffmpeg.start();
//
//                if (!original.delete()) {
//                    log.warn("Video " + original + " hasn't been deleted");
//                }
//
//                ffmpeg.saveOutputToFile(result);
//
//                log.info("Video " + original + " converted to " + result);
//
//                return result;
            }

            @Override
            public String toString() {
                return "ContainerWebDriver(" + capabilities + ")";
            }

            @Override
            public void close() {
                // already closed by afterTest method
            }

            private static class SimpleTestDescription implements TestDescription {
                private final String testId;
                private final String name;

                public SimpleTestDescription(String testId, String name) {
                    this.testId = testId;
                    this.name = name;
                }

                @Override
                public String getTestId() {
                    return testId;
                }

                @Override
                public String getFilesystemFriendlyName() {
                    return name.replaceAll("\"", "'");
                }

                @Override
                public String toString() {
                    return "SimpleTestDescription(" +
                            "testId:'" + testId + '\'' +
                            ", name:'" + name + '\'' +
                            ')';
                }
            }
        }
    }

}
