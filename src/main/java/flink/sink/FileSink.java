package flink.sink;


import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileSink {

    public static void main(String[] args) throws Exception {
        // 1. Utworzenie środowiska Flink
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH); // lub STREAMING, zależnie od potrzeb

        // 2. Pierwszy strumień: tylko print()
        DataStream<Integer> firstStream = env.fromElements(1, 2, 3, 4, 5);
        firstStream.print();

        env.execute("Print Stream Example");

        // 3. Drugi strumień: zapis do pliku
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        DataStream<String> textStream = env.fromCollection(List.of("as", "bb", "cc", "dd"));

        // 4. Konfiguracja FileSink
        StreamingFileSink<String> sink = StreamingFileSink
                .forRowFormat(new Path("output"), new SimpleStringEncoder<String>("UTF-8"))
                .withOutputFileConfig(
                        OutputFileConfig.builder()
                                .withPartPrefix("part")
                                .withPartSuffix(".txt")
                                .build()
                )
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(TimeUnit.SECONDS.toMillis(15))
                                .withInactivityInterval(TimeUnit.SECONDS.toMillis(5))
                                .withMaxPartSize(1024 * 1024 * 128)
                                .build()
                )
                .build();

        // 5. Podpięcie sinka
        textStream.addSink(sink);

        env.execute("FileSink Example");
    }
}
