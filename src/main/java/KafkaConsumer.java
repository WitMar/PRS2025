import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class KafkaConsumer {

    public static void main(String[] args) {
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String bootstrapServers = host + ":" + port;
        String topic = "zaj2_2";
        String groupId = "demo";

        String sasl_username = "uam";
        String sasl_password = "uamuamuam";
        String truststore_location = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
        String truststore_password = "oiGDQg1y";
        String keystore_location = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
        String keystore_password = "oiGDQg1y";

        String boostrap_servers = String.format("%s:%d", host, port);
        // you can create topic in control panel

        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrap_servers);

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // configure the following settings for SSL Connection
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststore_location);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststore_password);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystore_location);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystore_password);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        // Kafka java client must have a group for consumer.
        // You can specify a group name at will, and kafka server will create
        // the consumer group for you if it doesn't exist
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "abc");

        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);

        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(),
                            record.value());
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        consumer.close();

    }

}