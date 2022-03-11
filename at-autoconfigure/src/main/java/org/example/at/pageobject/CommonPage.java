package org.example.at.pageobject;

import io.qameta.atlas.webdriver.extension.FindBy;
import io.qameta.atlas.webdriver.extension.Name;
import io.qameta.atlas.webdriver.extension.Param;
import org.example.at.webdriver.element.AtWebElement;
import org.example.at.webdriver.element.AtWebPage;
import org.example.at.webdriver.element.InputTextElement;
import org.example.at.webdriver.element.SelectElement;
import org.example.at.webdriver.element.SliderElement;
import org.example.at.webdriver.element.SuggestionsInputTextElement;

public interface CommonPage extends AtWebPage {
    @Name("элемент {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    AtWebElement element(@Param("id") String id);

    @Name("кнопка {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    AtWebElement button(@Param("id") String id);

    @Name("чекбокс {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    AtWebElement checkbox(@Param("id") String id);

    @Name("поле {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    InputTextElement textInput(@Param("id") String id);

    @Name("список {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    SelectElement select(@Param("id") String id);

    @Name("слайдер {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    SliderElement slider(@Param("id") String id);

    @Name("поле {{ id }}")
    @FindBy("//*[@id=\"{{ id }}\"]")
    SuggestionsInputTextElement suggestionsTextInput(@Param("id") String id);
}
