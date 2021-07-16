package com.amdocs.aia.il.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RTPUtils {

    private RTPUtils() {
        // Utils class
        throw new IllegalStateException("Util Class");
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }


}
