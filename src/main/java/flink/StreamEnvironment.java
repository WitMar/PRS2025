package flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

public class StreamEnvironment {

    public static void main(String[] args) throws Exception {

        // 1. Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. Define a data source (e.g., read from a text file)
        DataStream<String> text = env.fromData(List.of("lower", "UPPER", "MiDdLe"));

        // 3. Transform the data (e.g., map each line to its uppercase)
        DataStream<String> upperCaseText = text.map(line -> line.toUpperCase());

        // 4. (Optional) Define a sink (e.g., write to another text file)
        DataStreamSink<String> sink = upperCaseText.print();

        // 5. Execute the program
        env.execute("Simple Flink Application");
    }
}