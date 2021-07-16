package com.amdocs.aia.il.common.utils;

import java.io.*;

public final class DbUtils {
    private DbUtils() {
        // Utility class
    }

    public static byte[] serialize(final Serializable serializable) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(serializable);
            return out.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static <T> T deserialize(final byte[] bytes) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             final ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            throw new RuntimeException("Exception occurred while deserializing", e);
        }
    }
}