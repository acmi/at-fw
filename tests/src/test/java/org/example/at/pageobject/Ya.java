package org.example.at.pageobject;

import io.qameta.atlas.webdriver.extension.FindBy;
import io.qameta.atlas.webdriver.extension.Name;
import org.example.at.webdriver.element.AtWebElement;

public interface Ya extends CommonPage {
    default AtWebElement getTextInput() {
        return textInput("text");
    }

    @Name("кнопка Найти")
    @FindBy("/html/body/table/tbody/tr[2]/td/form/div[2]/button")
    AtWebElement getSearchButton();
}
