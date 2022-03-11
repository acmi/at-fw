package org.example.at.autoconfigure.webdriver;

import io.qameta.atlas.core.Atlas;
import io.qameta.atlas.core.api.MethodExtension;
import io.qameta.atlas.core.internal.Configuration;
import io.qameta.atlas.webdriver.context.WebDriverContext;
import io.qameta.atlas.webdriver.extension.DriverProviderExtension;
import io.qameta.atlas.webdriver.extension.ExecuteJScriptMethodExtension;
import io.qameta.atlas.webdriver.extension.FindByCollectionExtension;
import io.qameta.atlas.webdriver.extension.FindByExtension;
import io.qameta.atlas.webdriver.extension.PageExtension;
import io.qameta.atlas.webdriver.extension.ShouldMethodExtension;
import io.qameta.atlas.webdriver.extension.WaitUntilMethodExtension;
import io.qameta.atlas.webdriver.extension.WrappedElementMethodExtension;
import org.example.at.atlas.core.internal.DefaultMethodExtension;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
//import ru.omay.at.atlas.webdriver.extension.FindByExtension;
//import ru.omay.at.atlas.webdriver.extension.SelectExtension;

import java.util.Collection;

@Lazy
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({Atlas.class, WebDriverContext.class})
public class WebDriverAtlasAutoConfiguration {

    @Bean
    public DriverProviderExtension driverProviderExtension() {
        return new DriverProviderExtension();
    }

    @Bean
    public DefaultMethodExtension defaultMethodExtension() {
        return new DefaultMethodExtension();
    }

//    @Bean
//    public SelectExtension selectExtension() {
//        return new SelectExtension();
//    }

    @Bean
    public FindByExtension findByExtension() {
        return new FindByExtension();
    }

    @Bean
    public FindByCollectionExtension findByCollectionExtension() {
        return new FindByCollectionExtension();
    }

    @Bean
    public ShouldMethodExtension shouldMethodExtension() {
        return new ShouldMethodExtension();
    }

    @Bean
    public WaitUntilMethodExtension waitUntilMethodExtension() {
        return new WaitUntilMethodExtension();
    }

    @Bean
    public WrappedElementMethodExtension wrappedElementMethodExtension() {
        return new WrappedElementMethodExtension();
    }

    @Bean
    public ExecuteJScriptMethodExtension executeJScriptMethodExtension() {
        return new ExecuteJScriptMethodExtension();
    }

    @Bean
    public PageExtension pageExtension() {
        return new PageExtension();
    }

    @Bean
    @Scope("cucumber-glue")
    public Atlas atlas(WebDriver webDriver, Collection<MethodExtension> extensions) {
        var configuration = new Configuration();
        configuration.registerContext(new WebDriverContext(webDriver));
        extensions.forEach(configuration::registerExtension);

        return new Atlas(configuration);
    }

}
