package simpleReader;

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

import java.util.Properties;

public class StatelessPrint {
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
        // ToDo change the name
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "zzz");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);

        // This helps read from the beginning when no committed offset exists
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Build topology
        StreamsBuilder builder = new StreamsBuilder();
        // Topic: "my-topic", Key: String, Value: String
        KStream<String, String> stream = builder.stream(
                "zaj2_2",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        // Example: Print every record
        stream.foreach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
