package flink.processFunction;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class GlobalWindowWithProcessFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Input: Tuple2<timestamp, value>
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

        // Use GlobalWindow + CountTrigger + ProcessWindowFunction
        DataStream<String> result = withTimestamps
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(2))
                .process(new AverageProcessFunction());

        result.print();

        env.execute("Global Window with ProcessWindowFunction");
    }

    // Custom ProcessAllWindowFunction to compute average
    public static class AverageProcessFunction extends ProcessAllWindowFunction<
            Tuple2<Long, Integer>, // input type
            String,                // output type
            GlobalWindow> {        // window type

        @Override
        public void process(Context context, Iterable<Tuple2<Long, Integer>> elements, Collector<String> out) {
            int sum = 0;
            int count = 0;
            for (Tuple2<Long, Integer> element : elements) {
                sum += element.f1;
                count++;
            }
            double average = count == 0 ? 0.0 : ((double) sum / count);
            out.collect("Window fired: average = " + average);
        }
    }
}

