package com.amdocs.aia.il.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

public final class LogUtils {
    private LogUtils() {
        // singleton
    }

    private static final StringBuilder OUTPUT = new StringBuilder();

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable t) {
        log(message, t, true);
    }

    public static String getOutput() {
        return OUTPUT.toString();
    }

    public static synchronized void reset() {
        OUTPUT.delete(0, OUTPUT.length());
    }

    private static String getCurrentTime() {
        Date now = new Date();
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(now);
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    private static synchronized void log(String s, Throwable t, boolean logToStandardOut) {
        int startIdx = OUTPUT.length();
        OUTPUT.append(getCurrentTime()).append(" - ").append(s);
        if (t != null) {
            OUTPUT.append("\n").append(getStackTrace(t));
        }
        if (logToStandardOut) {
            if (startIdx != 0) {
                startIdx--;
            }
            System.out.println(OUTPUT.substring(startIdx, OUTPUT.length()));
        }

        OUTPUT.append('\n');
    }
}