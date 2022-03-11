package org.example.at.webdriver.element;

import io.qameta.atlas.webdriver.extension.FindBy;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface SuggestionsInputTextElement extends InputTextElement {

    @FindBy("//*[@data-component=\"Options\"]//*[@data-component=\"OptionsItem\"]")
    List<AtWebElement> getItems();

    default void typeAndSelectFirst(String text) {
        typeAndSelect(text, 0);
    }

    default void typeAndSelect(String text, int index) {
        setText(text);
        select(index);
    }

    default void typeAndSelect(String text, Predicate<String> textPredicate) {
        setText(text);
        select(textPredicate);
    }

    default List<String> getSuggestions() {
        return getItems().stream()
                .map(AtWebElement::getText)
                .collect(Collectors.toList());
    }

    default void select(int index) {
        getItems().get(index).click();
    }

    default void select(Predicate<String> textPredicate) {
        getItems().stream()
                .filter(it -> textPredicate.test(it.getText()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(null))
                .click();
    }

}
