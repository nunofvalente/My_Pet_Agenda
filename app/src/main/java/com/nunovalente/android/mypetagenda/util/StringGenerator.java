package com.nunovalente.android.mypetagenda.util;

import java.util.Random;

public class StringGenerator {

    public static String getRandomString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-!_";
        StringBuilder builder = new StringBuilder();
        Random rnd = new Random();
        while (builder.length() < 10) {
            int index = (int) (rnd.nextFloat() * chars.length());
            builder.append(chars.charAt(index));
        }
        return builder.toString();
    }

    public static String getAccountIdStringBuilder(String text) {
        return ("Account Code: " + text);
    }

    public static String getReminderTimeStringBuilder(int hours, int minutes) {
        return (hours + ":" + minutes);
    }

    public static String getPetWeightBuilder(String weight) {
        return (weight + " Kg");
    }
}
