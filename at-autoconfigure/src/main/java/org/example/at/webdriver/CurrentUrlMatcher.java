package org.example.at.webdriver;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.openqa.selenium.WebDriver;

public class CurrentUrlMatcher extends TypeSafeMatcher<WebDriver> {

    private final Matcher<String> matcher;

    public CurrentUrlMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(WebDriver webDriver) {
        return matcher.matches(webDriver.getCurrentUrl());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("webdriver has url with text ").appendDescriptionOf(matcher);
    }

    @Override
    protected void describeMismatchSafely(WebDriver webDriver, Description mismatchDescription) {
        matcher.describeMismatch(webDriver.getCurrentUrl(), mismatchDescription);
    }

    public static Matcher<WebDriver> currentUrl(Matcher<String> matcher) {
        return new CurrentUrlMatcher(matcher);
    }

}
