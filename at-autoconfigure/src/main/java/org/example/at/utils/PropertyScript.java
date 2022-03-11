package org.example.at.utils;

import groovy.lang.Script;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.UUID;

public abstract class PropertyScript extends Script {

    public String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public String formatPhone(String phone) {
        return FormatUtils.formatPhoneRu(phone);
    }

    public String formatDateTime(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporal);
    }

    public LocalDate currentDate() {
        return LocalDate.now();
    }

    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    public Duration duration(String duration) {
        return Duration.parse(duration);
    }

    public LocalDate date(String text) {
        return LocalDate.parse(text);
    }

    public LocalDateTime dateTime(String text) {
        return LocalDateTime.parse(text);
    }

}
