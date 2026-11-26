package com.example.kafka;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Последние сообщения, которые получил этот консьюмер. */
public final class MessageStore {

    private static final int MAX = 100;
    private static final LinkedList<String> items = new LinkedList<String>();
    private static volatile String status = "консьюмер не запущен";

    private MessageStore() {
    }

    public static synchronized void add(String from, String type, int partition, long offset, String value) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String json = "{\"time\":" + Json.str(time)
                + ",\"from\":" + Json.str(from)
                + ",\"type\":" + Json.str(type)
                + ",\"partition\":" + partition
                + ",\"offset\":" + offset
                + ",\"value\":" + Json.str(value) + "}";
        items.addFirst(json);
        while (items.size() > MAX) {
            items.removeLast();
        }
    }

    public static void status(String value) {
        status = value;
    }

    public static String status() {
        return status;
    }

    public static synchronized String json() {
        List<String> copy = new ArrayList<String>(items);
        return "{\"status\":" + Json.str(status) + ",\"messages\":[" + String.join(",", copy) + "]}";
    }
}
