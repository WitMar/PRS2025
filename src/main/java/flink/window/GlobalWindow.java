package flink.window;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

public class GlobalWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Input stream: (timestamp, value)
        DataStream<Tuple2<Long, Integer>> input = env.fromElements(
                Tuple2.of(1000L, 5),
                Tuple2.of(2000L, 10),
                Tuple2.of(3000L, 15),
                Tuple2.of(4000L, 20),
                Tuple2.of(5000L, 25),
                Tuple2.of(6000L, 30)
        );

        // Assign timestamps and watermark strategy
        DataStream<Tuple2<Long, Integer>> withTimestamps = input
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple2<Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((element, recordTimestamp) -> element.f0)
                );

        // Use GlobalWindow with CountTrigger of 5 (fires every 5 elements)
        DataStream<Double> result = withTimestamps
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(2))
                .aggregate(new AverageAggregate());

        result.print();

        env.execute("Global Window Average Example");
    }

    // Aggregate function to compute average
    public static class AverageAggregate implements AggregateFunction<Tuple2<Long, Integer>, Tuple2<Integer, Integer>, Double> {
        @Override
        public Tuple2<Integer, Integer> createAccumulator() {
            return Tuple2.of(0, 0); // sum, count
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

