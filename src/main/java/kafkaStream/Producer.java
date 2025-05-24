package kafkaStream;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.*;
import java.util.Properties;
import java.util.Random;

public class Producer {
    public static void main(String[] args) throws Exception {
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


            Random random = new Random();

            String[] sensorIds = {"sensor-1", "sensor-2", "sensor-3",
                    "sensor-4", "sensor-6", "sensor-8",
                    "sensor-5", "sensor-7", "sensor-9"};

            int i = 0;
            while (i < 2) {
                i++;
                String sensorId = sensorIds[random.nextInt(sensorIds.length)];
                double value = 10 + (random.nextDouble() * 20);

                producer.send(new ProducerRecord<>("zaj2_2", sensorId, String.valueOf(value)));

                System.out.println(sensorId + " " + value);
                //Thread.sleep(1000); // wysyłaj co 1 sekundę

            }

        } catch (Exception e) {

        }
    }
}