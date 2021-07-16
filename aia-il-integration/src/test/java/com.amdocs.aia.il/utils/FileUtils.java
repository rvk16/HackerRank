package com.amdocs.aia.il.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static com.amdocs.aia.il.utils.LogUtils.log;

public final class FileUtils {
    private FileUtils() {
        // singleton
    }

    public static void replaceStringInFolder(final String folder, final String fileSuffix, final String src, final String target) {
        try (final Stream<Path> stream = Files.walk(Paths.get(folder))) {
            stream.filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(fileSuffix))
                    .forEach(f -> replaceStringInFile(f.toFile().getAbsolutePath(), src, target));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void replaceStringInFile(final String file, final String src, final String target) {
        log("Replacing in file " + file + " src : " + src + " with target: " + target);
        final Path path = Paths.get(file);
        final Charset charset = StandardCharsets.UTF_8;
        try {
            String content = new String(Files.readAllBytes(path), charset);
            if (!content.contains(src)) {
                return;
            }
            content = content.replaceAll(src, target);
            Files.write(path, content.getBytes(charset));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getFileFromResourceDir(final String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        } else {
            URL resource = FileUtils.class.getClassLoader().getResource(fileName);
            if (resource != null) {
                return new File(Objects.requireNonNull(resource).getFile());
            }
        }
        throw new IllegalArgumentException("Could not find file " + fileName);
    }
}