package org.example.at.webdriver.element;

import io.qameta.atlas.webdriver.extension.FindBy;

import java.util.List;

public interface SliderElement extends AtWebElement {

    @FindBy(".//*[contains(@class, 'rc-slider-step')]//span[contains(@class, 'rc-slider-dot')]")
    List<AtWebElement> getDots();

    default int getValue() {
        return (int) getDots().stream()
                .filter(element -> element.getAttribute("class").contains("rc-slider-dot-active"))
                .count() - 1;
    }

    default void setValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Must be positive");
        }

        int max = getValueMax();
        if (value > max) {
            throw new IllegalArgumentException("Max value is " + max);
        }

        getDots().get(value).click();
    }

    default int getValueMax() {
        return getDots().size() - 1;
    }

    default double getValueRate() {
        return (double) getValue() / getValueMax();
    }

}
