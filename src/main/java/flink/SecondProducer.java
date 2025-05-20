package flink;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SecondProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String boostrap_servers = String.format("%s:%d", host, port);
        // you can create topic in control panel
        String sasl_username = "uam";
        String sasl_password = "uamuamuam";
        String truststore_password = "oiGDQg1y";
        System.setProperty("org.apache.kafka.common.security.authenticator", "DEBUG");
        System.setProperty("javax.net.debug", "ssl,handshake");

        String keystore_password = "oiGDQg1y";

        String truststore_location = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";

        String keystore_location = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";


        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrap_servers);

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        // configure the following settings for SSL Connection
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststore_location);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststore_password);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystore_location);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystore_password);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        String value = "this is a message";


        for (long index = 30; index < 40; index++) {
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>("data",
                    value + ": " + index);

            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Produce OK: " + metadata.toString());
        }


    }

}