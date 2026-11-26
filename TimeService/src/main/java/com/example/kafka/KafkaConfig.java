package com.example.kafka;

import java.net.InetAddress;

/**
 * Настройки Kafka. У всех семи компьютеров одна программа,
 * роль и адреса брокеров задаются переменными окружения.
 */
public final class KafkaConfig {

    private KafkaConfig() {
    }

    /** Адреса брокеров через запятую: 192.168.43.11:9092,192.168.43.12:9092,... */
    public static String bootstrap() {
        return value("KAFKA_BOOTSTRAP", "localhost:9092");
    }

    /** producer или consumer. */
    public static String role() {
        return value("KAFKA_ROLE", "producer").toLowerCase();
    }

    public static boolean isConsumer() {
        return role().contains("consumer");
    }

    public static String topic() {
        return value("KAFKA_TOPIC", "lab4-messages");
    }

    public static String nodeName() {
        String name = value("NODE_NAME", "");
        if (name.length() > 0) {
            return name;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "node";
        }
    }

    /** Разные группы — каждый консьюмер получает все сообщения. Одна группа — сообщения делятся. */
    public static String group() {
        return value("KAFKA_GROUP", "group-" + nodeName());
    }

    static String value(String name, String def) {
        String v = System.getenv(name);
        if (v == null || v.trim().length() == 0) {
            v = System.getProperty(name);
        }
        if (v == null || v.trim().length() == 0) {
            return def;
        }
        return v.trim();
    }
}
