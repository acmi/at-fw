package org.example.at.autoconfigure.webdriver;

public interface Scenario {
    String getName();

    String getId();

    boolean isFailed();

    void attach(byte[] data, String mimeType, String name);
}
