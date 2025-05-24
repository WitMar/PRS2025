package kafkaStream;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;

public class Window {

    public static void main(String[] args) {
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String bootstrapServers = host + ":" + port;
        String topic = "zaj2_2";
        String groupId = "demo";

        String username = "PR";
        String password = "uamuamuam";
        String truststoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
        String truststorePassword = "oiGDQg1y";
        String keystoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
        String keystorePassword = "oiGDQg1y";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, username, password);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "zad2-1-window-max");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        // SSL truststore/keystore
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> source = builder.stream("zaj2_1");

        KStream<String, Double> parsed = source
                .map((key, value) -> {
                    try {
                        JsonNode node = mapper.readTree(value);
                        String sensorId = node.get("sensorId").asText();
                        double val = node.get("value").asDouble();
                        return new KeyValue<>(sensorId, val);
                    } catch (Exception e) {
                        System.err.println("Błąd parsowania: " + e.getMessage());
                        return new KeyValue<>("parse-error", 0.0);
                    }
                });

        KGroupedStream<String, Double> grouped = parsed.groupByKey();

        TimeWindows windows = TimeWindows.of(Duration.ofSeconds(1));

        KTable<Windowed<String>, Double> maxPerWindow = grouped
                .windowedBy(windows)
                .reduce(
                        Double::max,
                        Materialized.with(Serdes.String(), Serdes.Double())
                );

        // Wypisanie wyników na ekran
        maxPerWindow
                .toStream()
                .foreach((windowedKey, maxValue) -> {
                    String sensorId = windowedKey.key();
                    long windowStart = windowedKey.window().start();
                    long windowEnd = windowedKey.window().end();
                    System.out.printf(
                            "Sensor: %s | Window: %d - %d | Max value: %.2f%n",
                            sensorId, windowStart, windowEnd, maxValue
                    );
                });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
