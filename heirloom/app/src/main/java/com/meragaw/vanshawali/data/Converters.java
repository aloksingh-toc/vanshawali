package com.meragaw.vanshawali.data;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Stores a List<String> column as a single "|"-delimited string. */
public class Converters {

    private static final String DELIMITER = "\\|";
    private static final String JOINER = "|";

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(JOINER);
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(value.split(DELIMITER)));
    }
}
