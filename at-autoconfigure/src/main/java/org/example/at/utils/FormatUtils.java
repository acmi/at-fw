package org.example.at.utils;

public class FormatUtils {

    private FormatUtils() {
    }

    public static String formatPhoneRu(String phone) {
        if (phone == null) {
            return null;
        }

        if (phone.length() == 10) {
            return phone.replaceFirst("(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "+7 ($1) $2-$3-$4");
        } else if (phone.length() == 11 && phone.startsWith("7")) {
            return phone.replaceFirst("(\\d)(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "+$1 ($2) $3-$4-$5");
        }

        throw new IllegalArgumentException(phone);
    }

}
