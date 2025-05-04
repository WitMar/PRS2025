package flink.operators;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Transformations {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1. Map
        System.out.println("----- Map -----");
        DataStream<Integer> numbers = env.fromElements(1, 2, 3, 4, 5);
        numbers
                .map((MapFunction<Integer, Integer>) value -> value * 2)
                .returns(Integer.class)
                .print();
        env.execute("Map Example");

        Thread.sleep(2000);
        System.out.println("-----");

        // 2. FlatMap
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.out.println("----- FlatMap -----");
        DataStream<String> textLines = env.fromElements("hello apache flink", "streaming compute");
        textLines
                .flatMap((FlatMapFunction<String, String>) (line, out) -> {
                    for (String word : line.split(" ")) {
                        out.collect(word);
                    }
                })
                .returns(String.class)
                .print();
        env.execute("FlatMap Example");

        Thread.sleep(2000);
        System.out.println("-----");

        // 3. Filter
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.out.println("----- Filter -----");
        DataStream<Integer> intStream = env.fromElements(0, 1, 2, 3, 4, 5);
        intStream
                .filter((FilterFunction<Integer>) value -> value != 0)
                .print();
        env.execute("Filter Example");

        System.out.println("-----");

        // 4. KeyBy (no aggregation, just logical partitioning)
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.out.println("----- KeyBy -----");
        DataStream<Tuple2<Integer, String>> tuples1 = env.fromElements(
                Tuple2.of(1, "a"),
                Tuple2.of(2, "a"),
                Tuple2.of(3, "b")
        );
        tuples1
                .keyBy((KeySelector<Tuple2<Integer, String>, String>) value -> value.f1)
                .print();
        env.execute("KeyBy Example");

        Thread.sleep(2000);
        System.out.println("-----");

        // 5. Reduce
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.out.println("----- Reduce -----");
        DataStream<Tuple2<Integer, String>> tuples2 = env.fromElements(
                Tuple2.of(1, "a"),
                Tuple2.of(2, "a"),
                Tuple2.of(3, "a"),
                Tuple2.of(4, "b")
        );
        tuples2
                .keyBy((KeySelector<Tuple2<Integer, String>, String>) value -> value.f1)
                .reduce((ReduceFunction<Tuple2<Integer, String>>) (a, b) ->
                        Tuple2.of(a.f0 + b.f0, b.f1)
                )
                .print();
        env.execute("Reduce Example");

        Thread.sleep(2000);
        System.out.println("-----");

        // 6. PartitionCustom
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.out.println("----- PartitionCustom -----");
        int partition = 3;
        DataStream<Tuple2<Integer, String>> tuples3 = env.fromElements(
                Tuple2.of(2, "a"),
                Tuple2.of(2, "c"),
                Tuple2.of(3, "b")
        );

        tuples3
                .partitionCustom(
                        (Partitioner<Integer>) (key, numPartitions) -> key % partition,
                        (KeySelector<Tuple2<Integer, String>, Integer>) value -> value.f0
                )
                .print();

        env.execute("PartitionCustom Example");

        System.out.println("-----");
    }
}

