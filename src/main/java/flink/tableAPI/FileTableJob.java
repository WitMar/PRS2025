package flink.tableAPI;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

public class FileTableJob {
    public static void main(String[] args) {
        // Create TableEnvironment
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inBatchMode() // or inStreamingMode()
                .build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // Register CSV table (no Row, no raw format)
        tableEnv.executeSql("""
            CREATE TABLE people (
                id INT,
                name STRING
            ) WITH (
                'connector' = 'filesystem',
                'path' = 'cities.csv',
                'format' = 'csv'
            )
        """);

        // SQL query
        Table result = tableEnv.sqlQuery("SELECT id, UPPER(name) AS upper_name FROM people");

        // Print results
        result.execute().print();
    }
}
