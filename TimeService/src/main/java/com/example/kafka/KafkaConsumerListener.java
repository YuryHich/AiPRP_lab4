package com.example.kafka;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import websocket.chat.ChatAnnotation;

/**
 * Запускается вместе с Tomcat. Если роль consumer — в фоне читает топик
 * и пересылает каждое сообщение на страницу консьюмера и в веб-сокет чат.
 */
@WebListener
public class KafkaConsumerListener implements ServletContextListener {

    private volatile boolean running;
    private volatile KafkaConsumer<String, String> consumer;
    private Thread thread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!KafkaConfig.isConsumer()) {
            MessageStore.status("роль " + KafkaConfig.role() + ", консьюмер не нужен");
            return;
        }
        running = true;
        thread = new Thread(this::loop, "kafka-consumer");
        thread.setDaemon(true);
        thread.start();
    }

    private void loop() {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.bootstrap());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConfig.group());
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-" + KafkaConfig.nodeName());
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // новый консьюмер читает топик с начала
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        consumer = new KafkaConsumer<String, String>(p);
        consumer.subscribe(Collections.singletonList(KafkaConfig.topic()));
        MessageStore.status("подключение к " + KafkaConfig.bootstrap() + ", группа " + KafkaConfig.group());
        try {
            while (running) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    if (!consumer.assignment().isEmpty()) {
                        MessageStore.status("читаю " + KafkaConfig.topic() + ", группа " + KafkaConfig.group()
                                + ", партиции " + consumer.assignment());
                    }
                    for (ConsumerRecord<String, String> r : records) {
                        String from = header(r, "from");
                        String type = header(r, "type");
                        MessageStore.add(from, type, r.partition(), r.offset(), r.value());
                        ChatAnnotation.publish("Kafka от " + from + " (партиция " + r.partition() + "): " + r.value());
                    }
                } catch (WakeupException e) {
                    break;
                } catch (Exception e) {
                    MessageStore.status("ошибка: " + e.getMessage());
                    Thread.sleep(3000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumer.close();
        }
    }

    private static String header(ConsumerRecord<String, String> r, String name) {
        Header h = r.headers().lastHeader(name);
        return h == null ? "?" : new String(h.value(), StandardCharsets.UTF_8);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
        KafkaSender.close();
    }
}
