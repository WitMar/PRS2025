package flink.source;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CsvReaderJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        // Define CSV schema (matches the CSV file and POJO field order)
        CsvSchema schema = CsvSchema.builder()
                .addColumn("id", CsvSchema.ColumnType.NUMBER)
                .addColumn("name", CsvSchema.ColumnType.STRING)
                .setColumnSeparator(',')
                .build();

        // Use forSchema with POJO class type
        CsvReaderFormat<City> csvFormat = CsvReaderFormat.forSchema(
                schema,
                TypeInformation.of(City.class)
        );

        FileSource<City> source = FileSource
                .forRecordStreamFormat(csvFormat, new Path("cities.csv"))
                .build();

        DataStreamSource<City> stream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "csv-source"
        );

        stream.print();

        env.execute("CSV Reader With POJO");
    }

    // POJO class must be public and have no-arg constructor
    public static class City {
        public long id;
        public String name;

        public City() {
        } // required

        public City(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{id=" + id + ", name='" + name + "'}";
        }
    }
}