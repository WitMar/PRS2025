package flink.window;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

public class TumblingWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Sample input: Tuple2<timestamp, value>
        DataStream<Tuple2<Long, Integer>> input = env.fromElements(
                Tuple2.of(1000L, 5),
                Tuple2.of(2000L, 10),
                Tuple2.of(3000L, 15),
                Tuple2.of(12000L, 20),
                Tuple2.of(15000L, 25)
        );

        // Assign timestamps and watermarks
        DataStream<Tuple2<Long, Integer>> withTimestamps = input
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple2<Long, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(4))
                                .withTimestampAssigner((element, recordTimestamp) -> element.f0)
                );

        // Tumbling window on event time (e.g., 10s)
        DataStream<Double> result = withTimestamps
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
                .aggregate(new AverageAggregate());

        result.print();

        env.execute("Tumbling Event Time Window Average");
    }

    // Aggregates (sum, count) → average
    public static class AverageAggregate implements AggregateFunction<Tuple2<Long, Integer>, Tuple2<Integer, Integer>, Double> {
        @Override
        public Tuple2<Integer, Integer> createAccumulator() {
            return Tuple2.of(0, 0); // (sum, count)
        }

        @Override
        public Tuple2<Integer, Integer> add(Tuple2<Long, Integer> value, Tuple2<Integer, Integer> acc) {
            return Tuple2.of(acc.f0 + value.f1, acc.f1 + 1);
        }

        @Override
        public Double getResult(Tuple2<Integer, Integer> acc) {
            return acc.f1 == 0 ? 0.0 : ((double) acc.f0) / acc.f1;
        }

        @Override
        public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b) {
            return Tuple2.of(a.f0 + b.f0, a.f1 + b.f1);
        }
    }
}

