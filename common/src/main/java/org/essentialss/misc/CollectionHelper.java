package org.essentialss.misc;

import java.util.Collection;

public final class CollectionHelper {

    private CollectionHelper() {
        throw new RuntimeException("Should not generate");
    }

    public static <T> boolean match(Collection<T> original, Collection<T> compare) {
        if (original.size() != compare.size()) {
            return false;
        }
        return compare.containsAll(original);
    }

}
