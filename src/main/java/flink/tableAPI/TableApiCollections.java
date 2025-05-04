package flink.tableAPI;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.types.Row;

public class TableApiCollections {
    public static void main(String[] args) {
        // Create TableEnvironment with streaming mode (Blink is default)
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // Create a table from inline values with explicit schema
        Table input = tableEnv.fromValues(
                DataTypes.ROW(
                        DataTypes.FIELD("id", DataTypes.INT()),
                        DataTypes.FIELD("name", DataTypes.STRING())
                ),
                Row.of(1, "Alice"),
                Row.of(2, "Bob"),
                Row.of(3, "Charlie")
        );

        tableEnv.createTemporaryView("people", input);

        // Query and print
        Table result = tableEnv.sqlQuery("SELECT id, UPPER(name) as upper_name FROM people");
        result.execute().print();
    }
}
