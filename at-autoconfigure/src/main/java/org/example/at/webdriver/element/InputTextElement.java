package org.example.at.webdriver.element;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;

public interface InputTextElement extends AtWebElement {

    @Override
    default String getText() {
        return getAttribute("value");
    }

    @Override
    default void clear() {
        sendKeys(Keys.END);

        while (getText().length() > 0) {
            sendKeys(Keys.BACK_SPACE);
        }
    }

    default void setText(String text) {
        clear();

        if (!StringUtils.isEmpty(text)) {
            sendKeys(text);
        }
    }

}
