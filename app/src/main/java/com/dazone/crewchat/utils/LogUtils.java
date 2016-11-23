package com.dazone.crewchat.utils;

import android.util.Log;

/**
 * Created by Admin on 7/13/2016.
 */
public class LogUtils {
    static String TAG = "LongLogCat";
    public static void logMultilineString(String data) {
        for (String line : data.split("\n")) {
            logLargeString(line);
        }
    }

    public static void logLargeString(String data) {
        final int CHUNK_SIZE = 4076;  // Typical max logcat payload.
        int offset = 0;
        while (offset + CHUNK_SIZE <= data.length()) {
            Log.d(TAG, data.substring(offset, offset += CHUNK_SIZE));
        }
        if (offset < data.length()) {
            Log.d(TAG, data.substring(offset));
        }
    }
}
