package flink.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.*;


public class CSVSink {

    public static void main(String[] args) throws Exception {
        // 1. Konfiguracja TableEnvironment
        Configuration config = new Configuration();
        config.setString("python.executable", "python3"); // opcjonalnie
        config.setString("python.client.executable", "python3"); // opcjonalnie

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inBatchMode()
                .build();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // 2. Wszystkie dane do jednego pliku
        tableEnv.getConfig().getConfiguration().setString("parallelism.default", "1");

        // 3. CREATE TABLE countries (czyta CSV z pliku)
        tableEnv.executeSql("""
                    CREATE TABLE countries (
                        code STRING,
                        name STRING,
                        continent STRING,
                        wikipedia_link STRING
                    ) WITH (
                        'connector' = 'filesystem',
                        'path' = 'countries.csv',
                        'format' = 'csv'
                    )
                """);

        // 4. Wczytaj dane z tabeli
        Table tableInput = tableEnv.from("countries");

        // 5. Wypisz schemat wejściowy
        System.out.println("\nSchema of input table:");
        System.out.println(tableInput.getResolvedSchema());

        // 6. CREATE TABLE resultTable przez TableDescriptor
        TableDescriptor resultTableDescriptor = TableDescriptor
                .forConnector("filesystem")
                .schema(Schema.newBuilder()
                        .column("code", DataTypes.STRING())
                        .column("name", DataTypes.STRING())
                        .column("continent", DataTypes.STRING())
                        .column("wiki", DataTypes.STRING())
                        .build())
                .option("path", "result")
                .format("csv")
                .build();

        tableEnv.createTemporaryTable("resultTable", resultTableDescriptor);

        // 7. Odczyt i wypisanie schematu tabeli wynikowej
        Table tableOutput = tableEnv.from("resultTable");
        System.out.println("\nSchema of result table:");
        System.out.println(tableOutput.getResolvedSchema());

        // 8. Wstaw dane
        tableInput.executeInsert("resultTable").await();
    }
}

