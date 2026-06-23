package in.ibrahimabad.vanshawali.common.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/** Small helpers for the repeated findAll().stream().sorted(...) pattern across services. */
public final class Sorting {

    private Sorting() {}

    public static <T, K extends Comparable<? super K>> List<T> byKeyAsc(List<T> items, Function<T, K> keyExtractor) {
        return items.stream().sorted(Comparator.comparing(keyExtractor)).toList();
    }

    public static <T, K extends Comparable<? super K>> List<T> byKeyDesc(List<T> items, Function<T, K> keyExtractor) {
        return items.stream().sorted(Comparator.comparing(keyExtractor).reversed()).toList();
    }
}
