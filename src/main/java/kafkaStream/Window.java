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
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;
import java.util.Properties;

public class Window {

    public static void main(String[] args) {
        // Connection details
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String bootstrapServers = host + ":" + port;

        String username = "uam";
        String password = "uamuamuam";
        String truststoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
        String truststorePassword = "oiGDQg1y";
        String keystoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
        String keystorePassword = "oiGDQg1y";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, username, password);
        System.setProperty("org.apache.kafka.streams.processor.internals.StreamThread", "DEBUG");
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "window");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);

        // This helps read from the beginning when no committed offset exists
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapper mapper = new ObjectMapper();

        KStream<String, String> source = builder.stream(
                "zaj2_2",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        KStream<String, Double> parsed = source
                .map((key, value) -> {
                    try {
                        double val = Double.parseDouble(value);
                        return new KeyValue<>(key, val);
                    } catch (Exception e) {
                        System.err.println("Błąd parsowania: " + e.getMessage() + " | Dane: " + value);
                        return new KeyValue<>("parse-error", 0.0);
                    }
                });

        KGroupedStream<String, Double> grouped = parsed.groupByKey(
                Grouped.with(Serdes.String(), Serdes.Double())
                        .withName("group-by-sensor")
        );

        TimeWindows windows = TimeWindows.of(Duration.ofSeconds(1));

        KTable<Windowed<String>, Double> maxPerWindow = grouped
                .windowedBy(windows)
                .reduce(
                        Double::max,
                        Materialized.<String, Double, WindowStore<Bytes, byte[]>>as("sensor-max-window-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Double())
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
