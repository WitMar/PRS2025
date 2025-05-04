package flink.tableAPI;


import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;

public class TableApiToDataStream {

    public static void main(String[] args) throws Exception {
        // 1. Create the environments
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 2. Define a DataStream source
        DataStream<String> inputStream = env.fromData(List.of("foo", "BAR", "BaZ"));

        // 3. Convert DataStream → Table with column name "word"
        Table wordTable = tableEnv.fromDataStream(inputStream, $("word"));

        // 4. Transform using Table API (UPPER function)
        Table upperTable = wordTable
                .select(call("UPPER", $("word")).as("upper_word"));

        // 5. Convert Table → DataStream<Row>
        DataStream<Row> resultStream = tableEnv.toDataStream(upperTable);

        // 6. Print result
        resultStream.print();

        // 7. Execute the pipeline
        env.execute("DataStream <-> Table Conversion Example");
    }
}
