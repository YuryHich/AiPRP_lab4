package com.example.service;

import java.util.HashMap;
import java.util.Map;

/** Словарь варианта 2. Им пользуются и REST, и веб-сокет. */
public class Dictionary {

    private static final Map<String, String> RUS_TO_ENG = new HashMap<String, String>();
    private static final Map<String, String> ENG_TO_RUS = new HashMap<String, String>();

    static {
        put("кот", "cat");
        put("собака", "dog");
        put("дом", "house");
        put("книга", "book");
        put("студент", "student");
        put("университет", "university");
        put("окно", "window");
        put("солнце", "sun");
        put("вода", "water");
        put("друг", "friend");
        put("школа", "school");
        put("яблоко", "apple");
        put("мама", "mother");
        put("папа", "father");
        put("город", "city");
        put("стол", "table");
        put("слово", "word");
    }

    private static void put(String rus, String eng) {
        RUS_TO_ENG.put(rus, eng);
        ENG_TO_RUS.put(eng, rus);
    }

    public static String translate(String word) {
        if (word == null) {
            return "";
        }
        String value = word.trim().toLowerCase();
        if (value.length() == 0) {
            return "";
        }
        String eng = RUS_TO_ENG.get(value);
        if (eng != null) {
            return eng;
        }
        String rus = ENG_TO_RUS.get(value);
        if (rus != null) {
            return rus;
        }
        return "не найдено";
    }

    public static String json(String word) {
        String translation = translate(word);
        return "{\"word\": \"" + escape(word) + "\", \"translation\": \"" + escape(translation) + "\"}";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
