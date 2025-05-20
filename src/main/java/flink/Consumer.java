package flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class Consumer {
    public static void main(String[] args) throws Exception {
        String host = "kafka-196813-0.cloudclusters.net";
        int port = 10294;
        String bootstrapServers = host + ":" + port;
        String topic = "pro";
        String groupId = "demo";

        String username = "uam";
        String password = "uamuamuam";
        String truststoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.truststore.jks";
        String truststorePassword = "oiGDQg1y";
        String keystoreLocation = "/home/marcin/App/PRS2025/src/main/resources/kafka.keystore.jks";
        String keystorePassword = "oiGDQg1y";

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasConfig = String.format(jaasTemplate, username, password);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<SensorRecord> kafkaSource = KafkaSource.<SensorRecord>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SensorRecordDeserializationSchema())

                // SASL_SSL config
                .setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
                .setProperty(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256")
                .setProperty(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig)
                .setProperty(ProducerConfig.ACKS_CONFIG, "1")
                // SSL truststore/keystore
                .setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation)
                .setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword)
                .setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation)
                .setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword)

                .build();

        // Create the Flink source stream
        DataStream<SensorRecord> stream = env.fromSource(
                kafkaSource,
                WatermarkStrategy.noWatermarks(),
                "Kafka SASL_SSL Source"
        );

        stream.addSink(new SinkFunction<SensorRecord>() {
            @Override
            public void invoke(SensorRecord value, Context context) {
                System.out.println(value);
            }
        });

        env.execute("Flink Kafka SASL_SSL Consumer");
    }
}