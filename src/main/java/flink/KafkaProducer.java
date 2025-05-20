package flink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.*;

public class KafkaProducer {

    private static final String API_URL =
            "https://data.sensor.community/airrohr/v1/filter/box=52.495056,16.802271,52.329310,17.047250";

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            System.out.println("Run");
            String host = "kafka-196813-0.cloudclusters.net";
            int port = 10294;
            String boostrap_servers = String.format("%s:%d", host, port);
            // you can create topic in control panel
            String sasl_username = "uam";
            String sasl_password = "uamuamuam";
            String truststorePassword = "oiGDQg1y";

            String keystorePassword = "oiGDQg1y";
            try {
                // Load keystore from resources inside JAR
                InputStream keystoreStream = KafkaProducer.class.getResourceAsStream("/kafka.keystore.jks");
                if (keystoreStream == null) {
                    throw new FileNotFoundException("Keystore not found in JAR resources");
                }
                InputStream truststoreStream = KafkaProducer.class.getResourceAsStream("/kafka.keystore.jks");
                if (truststoreStream == null) {
                    throw new FileNotFoundException("Keystore not found in JAR resources");
                }

// Write to temp file
                File tempKeystore = File.createTempFile("kafka", ".keystore.jks");
                tempKeystore.deleteOnExit();

                try (OutputStream out = new FileOutputStream(tempKeystore)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = keystoreStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                File tempTruststore = File.createTempFile("kafka", ".truststore.jks");
                tempTruststore.deleteOnExit();

                try (OutputStream out = new FileOutputStream(tempTruststore)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = truststoreStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }


                Properties props = new Properties();

                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrap_servers);

                String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
                String jaasConfig = String.format(jaasTemplate, sasl_username, sasl_password);
                props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
                props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
                props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

                // configure the following settings for SSL Connection
                props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, tempTruststore.getAbsolutePath());
                props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
                props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, tempKeystore.getAbsolutePath());
                props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
                props.put(ProducerConfig.ACKS_CONFIG, "1");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        "org.apache.kafka.common.serialization.StringSerializer");

                org.apache.kafka.clients.producer.KafkaProducer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);

                ObjectMapper mapper = new ObjectMapper();

                System.out.println("Gathering data");
                // Pobierz dane JSON
                InputStream is = null;

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

                        System.out.println("Sending data");
                        ProducerRecord<String, String> record = new ProducerRecord<>("pro", json);
                        Future<RecordMetadata> out = producer.send(record);
                        System.out.println("Wysłano do Kafki: " + out.get().toString());
                    }
                }

                producer.flush();
                producer.close();
            } catch (IOException | InterruptedException | ExecutionException e) {
                System.out.println(e.toString());
            }

        };

        // Initial delay = 0, then run every 5 minutes
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);

    }

}
