package flink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaProducer {

    private static final String API_URL =
            "https://data.sensor.community/airrohr/v1/filter/box=52.495056,16.802271,52.329310,17.047250";

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            String host = "kafka-196813-0.cloudclusters.net";
            int port = 10294;
            String boostrap_servers = String.format("%s:%d", host, port);
            // you can create topic in control panel
            String topic = "data";
            String sasl_username = "uam";
            String sasl_password = "uamuamuam";
            String truststoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
            String truststorePassword = "oiGDQg1y";
            String keystoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
            String keystorePassword = "oiGDQg1y";

            Properties props = new Properties();

            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrap_servers);

            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

            // configure the following settings for SSL Connection
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);

            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
            props.put(ProducerConfig.RETRIES_CONFIG, 5);
            props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);

            org.apache.kafka.clients.producer.KafkaProducer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);

            ObjectMapper mapper = new ObjectMapper();

            // Pobierz dane JSON
            InputStream is = null;
            try {
                is = new URL(API_URL).openStream();
                JsonNode root = mapper.readTree(is);

            for (JsonNode node : root) {
                long sensorId = node.get("sensor").get("id").asLong();
                double lat = node.get("location").get("latitude").asDouble();
                double lon = node.get("location").get("longitude").asDouble();
                String timestamp = node.get("timestamp").asText();

                for (JsonNode measurement : node.get("sensordatavalues")) {
                    String valueType = measurement.get("value_type").asText();
                    String value = measurement.get("value").asText();

                    String json = String.format(
                            "{\"sensorId\":%d,\"valueType\":\"%s\",\"value\":%s,\"latitude\":%.6f,\"longitude\":%.6f,\"timestamp\":\"%s\"}",
                            sensorId, valueType, value, lat, lon, timestamp
                    );

                    ProducerRecord<String, String> record = new ProducerRecord<>("data", Long.toString(sensorId), json);
                    producer.send(record);
                    System.out.println("Wysłano do Kafki: " + json);
                }
            }

            producer.flush();
            producer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        };

        // Initial delay = 0, then run every 5 minutes
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);

    }

}
