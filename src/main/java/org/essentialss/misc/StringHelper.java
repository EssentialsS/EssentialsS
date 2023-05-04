package org.essentialss.misc;

import org.jetbrains.annotations.NotNull;

public class StringHelper {

    public static void isIdFormat(@NotNull String id) {
        if (!id.equals(id.toLowerCase())) {
            throw new IllegalArgumentException("Uppercase is not allowed in ID");
        }
        if (id.contains(" ")) {
            throw new IllegalArgumentException("space is not allowed in ID");
        }
        if (id.contains(":")) {
            throw new IllegalArgumentException(": is not allowed in ID");
        }
    }

    public static String toIdFormat(@NotNull String name) {
        return name.toLowerCase().replaceAll(" ", "_");
    }
}
