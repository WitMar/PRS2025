package kafkaStream;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.Properties;

public class Average {
    public static void main(String[] args) throws Exception {
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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "average");
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

// Consume messages from topic as String keys and values
        KStream<String, String> sensorStream = builder.stream(
                "zaj2_2",
                Consumed.with(Serdes.String(), Serdes.String())
        );

// Aggregate to compute sum and count per key, stored as "sum,count"
        KTable<String, String> sensorAvg = sensorStream
                .mapValues(value -> {
                    try {
                        Double.parseDouble(value); // validate
                        return value;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number: " + value);
                        return "0";
                    }
                })
                .groupByKey()
                .aggregate(
                        () -> "0.0,0",  // initial value: "sum,count"
                        (key, newValue, aggregate) -> {
                            try {
                                double val = Double.parseDouble(newValue);
                                String[] parts = aggregate.split(",");
                                double sum = Double.parseDouble(parts[0]);
                                int count = Integer.parseInt(parts[1]);
                                sum += val;
                                count += 1;
                                return sum + "," + count;
                            } catch (Exception e) {
                                System.err.println("Aggregation error for key " + key + ": " + e.getMessage());
                                return aggregate;
                            }
                        },
                        Materialized.<String, String, org.apache.kafka.streams.state.KeyValueStore<org.apache.kafka.common.utils.Bytes, byte[]>>as("sensor-avg-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.String())                );

// Convert to stream and print computed average
        sensorAvg.toStream().foreach((sensorId, aggValue) -> {
            try {
                String[] parts = aggValue.split(",");
                double sum = Double.parseDouble(parts[0]);
                int count = Integer.parseInt(parts[1]);
                double avg = count > 0 ? sum / count : 0.0;
                System.out.printf("Sensor %s average: %.2f%n", sensorId, avg);
            } catch (Exception e) {
                System.err.println("Error printing average for " + sensorId + ": " + e.getMessage());
            }
        });

        // Start Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}