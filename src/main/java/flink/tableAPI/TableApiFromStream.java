package flink.tableAPI;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.List;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;

public class TableApiFromStream {

    public static void main(String[] args) throws Exception {
        // 1. Create environments
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 2. Define a source
        DataStream<String> inputStream = env.fromData(List.of("lower", "UPPER", "MiDdLe"));

        // 3. Convert DataStream to Table
        Table inputTable = tableEnv.fromDataStream(
                inputStream,
                $("text")
        );

        // 4. Apply transformation using Table API
        Table upperCaseTable = inputTable.select(
                call("UPPER", $("text")).as("upper_text")
        );

        // 5. Convert Table back to DataStream
        DataStream<Row> resultStream = tableEnv.toDataStream(upperCaseTable);

        // 6. Print the result
        resultStream.print();

        // 7. Execute
        env.execute("Table API Stream Example");
    }
}
