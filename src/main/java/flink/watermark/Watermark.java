package flink.watermark;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class Watermark {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> inputStream = env.fromData(List.of("a", "bb", "ccc", "dddd", "eeeee", "fffffff"));

        // Filter by severity and assign timestamps
        DataStream<String> withTimestamps = inputStream
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<String>forBoundedOutOfOrderness(Duration.ofMillis(1))
                                .withTimestampAssigner((event, timestamp) -> LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - event.length())
                );

        // Apply tumbling event-time window and reduce function
        withTimestamps
                .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(1)))
                .reduce(new ReduceFunction<String>() {
                    @Override
                    public String reduce(String a, String b) {
                        return "res";
                    }
                })
                .print();

        env.executeAsync("Windowed MyEvent Stream");

        Thread.sleep(2000);
    }
}

