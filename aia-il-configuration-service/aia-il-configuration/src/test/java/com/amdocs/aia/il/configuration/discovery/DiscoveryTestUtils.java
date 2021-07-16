package com.amdocs.aia.il.configuration.discovery;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class DiscoveryTestUtils {
    private DiscoveryTestUtils() {}

    public static InputStream csv(char delimiter, String... columns) {
        return IOUtils.toInputStream(String.join(String.valueOf(delimiter), columns));
    }

    public static InputStream testFile(String path) {
        try {
            return FileUtils.openInputStream(FileUtils.getFile("src/test/resources/" + path));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed opening input stream for " + path, e);
        }
    }
}
