package com.example.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringSerializer;

/** Продюсер: отправляет текст или HTML в топик. */
public final class KafkaSender {

    private static KafkaProducer<String, String> producer;
    private static boolean topicChecked = false;
    private static final AtomicLong counter = new AtomicLong();

    private KafkaSender() {
    }

    private static synchronized KafkaProducer<String, String> producer() {
        if (producer == null) {
            Properties p = new Properties();
            p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.bootstrap());
            p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            p.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-" + KafkaConfig.nodeName());
            // ждём подтверждения от всех живых реплик
            p.put(ProducerConfig.ACKS_CONFIG, "all");
            p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 10000);
            p.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);
            p.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
            producer = new KafkaProducer<String, String>(p);
        }
        return producer;
    }

    /** Создаёт топик, если его ещё нет: 3 партиции, реплик столько, сколько брокеров (не больше 3). */
    public static synchronized void ensureTopic() throws Exception {
        if (topicChecked) {
            return;
        }
        Properties p = new Properties();
        p.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.bootstrap());
        p.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);
        p.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 15000);
        try (AdminClient admin = AdminClient.create(p)) {
            if (!admin.listTopics().names().get(15, TimeUnit.SECONDS).contains(KafkaConfig.topic())) {
                int brokers = admin.describeCluster().nodes().get(15, TimeUnit.SECONDS).size();
                short replicas = (short) Math.max(1, Math.min(3, brokers));
                NewTopic topic = new NewTopic(KafkaConfig.topic(), 3, replicas);
                try {
                    admin.createTopics(Collections.singletonList(topic)).all().get(15, TimeUnit.SECONDS);
                } catch (ExecutionException e) {
                    if (!(e.getCause() instanceof TopicExistsException)) {
                        throw e;
                    }
                }
            }
        }
        topicChecked = true;
    }

    /** Ключ меняется с каждым сообщением, поэтому сообщения раскладываются по разным партициям. */
    public static RecordMetadata send(String type, String value) throws Exception {
        ensureTopic();
        String key = "msg-" + counter.incrementAndGet();
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(KafkaConfig.topic(), key, value);
        record.headers().add("type", type.getBytes(StandardCharsets.UTF_8));
        record.headers().add("from", KafkaConfig.nodeName().getBytes(StandardCharsets.UTF_8));
        return producer().send(record).get(30, TimeUnit.SECONDS);
    }

    public static synchronized void close() {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
