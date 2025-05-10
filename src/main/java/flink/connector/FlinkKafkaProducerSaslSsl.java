package flink.connector;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.security.scram.ScramLoginModule;

public class FlinkKafkaProducerSaslSsl {

    public static void main(String[] args) throws Exception {
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String bootstrapServers = host + ":" + port;
        String topic = "data";

        String username = "uam_pub";
        String password = "uamuamuam";
        String truststoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
        String truststorePassword = "oiGDQg1y";
        String keystoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
        String keystorePassword = "oiGDQg1y";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, username, password);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Sample stream data
        DataStream<String> stream = env.fromElements(
                "this is a message: 30",
                "this is a message: 31",
                "this is a message: 32",
                "this is a message: 33",
                "this is a message: 34",
                "this is a message: 35",
                "this is a message: 36",
                "this is a message: 37",
                "this is a message: 38",
                "this is a message: 39"
        );

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build())
                .setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
                .setProperty(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256")
                .setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig)
                .setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation)
                .setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword)
                .setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation)
                .setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword)
                .build();

        stream.sinkTo(sink);

        env.execute("Flink Kafka SASL_SSL Publisher");
    }
}
