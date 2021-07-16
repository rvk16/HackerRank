package com.amdocs.aia.il.common.utils;

public class SneakyThrowUtil {

    private SneakyThrowUtil(){}

    public static <T extends Exception, R> R sneakyThrow(Exception e) throws T {
        throw (T) e; // NOSONAR
    }

}
