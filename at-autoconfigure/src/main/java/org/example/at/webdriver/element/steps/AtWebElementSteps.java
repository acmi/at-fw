package org.example.at.webdriver.element.steps;

import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import io.qameta.atlas.core.Atlas;
import org.example.at.utils.DynamicContext;
import org.example.at.webdriver.AtlasUtils;
import org.example.at.webdriver.element.AtWebElement;
import org.example.at.webdriver.element.InputTextElement;
import org.example.at.webdriver.element.SuggestionsInputTextElement;

import static org.example.at.utils.Eval.eval;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static ru.yandex.qatools.matchers.webdriver.DisplayedMatcher.displayed;

public class AtWebElementSteps {

    private final AtWebElementStepsContext context;
    private final Atlas atlas;

    public AtWebElementSteps(DynamicContext context, Atlas atlas) {
        this.context = context.asType(AtWebElementStepsContext.class);
        this.atlas = atlas;
    }

    @Пусть("модель страницы {}")
    public void модель_страницы(final String pageClass) {
        try {
            context.setPage(atlas.create(AtlasUtils.getWebDriver(atlas), Class.forName("org.example.at.pageobject." + pageClass)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Page model not found: " + pageClass);
        }
    }

    @Тогда("проверить, что отображается элемент {}")
    public void проверить__что_отображается_элемент(String path) {
        assertThat(getPageElement(path), displayed());
    }

    @Пусть("текст элемента {} равен {}")
    public void текст_элемента_равен(String path, String groovyExpr) {
        var pageElement = getPageElement(path);
        if (pageElement instanceof SuggestionsInputTextElement) {
            ((SuggestionsInputTextElement) pageElement).typeAndSelect((String) eval(groovyExpr, context), 0);
        } else if (pageElement instanceof InputTextElement) {
            ((InputTextElement) pageElement).setText((String) eval(groovyExpr, context));
        } else {
            throw new IllegalStateException("Unsupported field " + pageElement.getClass().getSimpleName() + "(" + path + ")");
        }
    }

    @Тогда("проверить, что текст элемента {} равен {}")
    public void проверить__что_текст_элемента_равен(String path, String groovyExpr) {
        assertThat(getPageElement(path).getText(), equalTo((String) eval(groovyExpr, context)));
    }

    @Тогда("проверить, что текст элемента {} содержит {}")
    public void проверить__что_текст_элемента_содержит(String path, String groovyExpr) {
        assertThat(getPageElement(path).getText(), containsString((String) eval(groovyExpr, context)));
    }

    @Тогда("проверить, что текст элемента {} соответствует шаблону {}")
    public void проверить__что_текст_элемента_соответствует_шаблону(String path, String groovyExpr) {
        assertThat(getPageElement(path).getText(), matchesPattern((String) eval(groovyExpr, context)));
    }

    @Когда("осуществлен клик по элементу {}")
    public void осуществлен_клик_по_элементу(String path) {
        getPageElement(path).click();
    }

    private AtWebElement getPageElement(final String path) {
        assertThat("Page model is null, use модель_страницы method to set", context.getPage() != null);

        return (AtWebElement) eval("page." + path, context);
    }

}
