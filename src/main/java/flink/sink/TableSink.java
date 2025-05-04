package flink.sink;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import static org.apache.flink.table.api.Expressions.$;

public class TableSink {

    public static void main(String[] args) throws Exception {
        // 1. Tworzenie środowiska stream i TableEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 2. Tworzenie tabeli z danych w pamięci
        Table table = tableEnv.fromValues(
                DataTypes.ROW(
                        DataTypes.FIELD("id", DataTypes.INT()),
                        DataTypes.FIELD("name", DataTypes.STRING())
                ),
                Row.of(1, "ABC"),
                Row.of(2, "ABCDE")
        );

        // 3. (Opcjonalnie) wypisz schemat
        System.out.println("Schema:");
        System.out.println(table.getResolvedSchema());

        // 4. Rejestracja tabeli "print" (connector print)
        tableEnv.executeSql("""
                    CREATE TABLE print (
                        id INT,
                        data STRING
                    ) WITH (
                        'connector' = 'print'
                    )
                """);

        // 5. SELECT i insert do connectora print
        table.select($("id"), $("name").as("nazwa"))
                .executeInsert("print")
                .await();  // odpowiada .wait() z PyFlink
    }
}
