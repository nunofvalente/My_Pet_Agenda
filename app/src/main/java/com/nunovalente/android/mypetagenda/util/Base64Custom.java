package com.nunovalente.android.mypetagenda.util;

import android.util.Base64;

import java.util.Arrays;

public class Base64Custom {

    public static String encodeString(String text) {
        byte[] byteArray = text.getBytes();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
                .replace("(\\n|\\r)", "");
    }

    public static String decodeString(String text) {
        return Arrays.toString(Base64.decode(text.getBytes(), Base64.NO_WRAP));
    }
}
