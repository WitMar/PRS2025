package flink.process;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

public class Context {

    // Definicja tagu bocznego strumienia
    private static final OutputTag<String> lateOutputTag = new OutputTag<String>("late-data") {};

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Przykładowe dane: jedno z nich to "late"
        DataStream<String> input = env
                .fromElements("on-time", "late", "other")
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<String>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                .withTimestampAssigner((element, recordTimestamp) -> System.currentTimeMillis())
                );

        // Przetwarzanie z keyBy i KeyedProcessFunction
        SingleOutputStreamOperator<String> mainOutput = input
                .keyBy(value -> value.length() % 2 == 0 ? "even" : "odd")
                .process(new ExampleProcessFunction());

        // Drukowanie głównego strumienia
        mainOutput.print("MAIN");

        // Drukowanie side outputu
        mainOutput.getSideOutput(lateOutputTag).print("LATE");

        env.execute("Flink Context + Side Output Example");
    }

    // Funkcja przetwarzająca z użyciem Context
    public static class ExampleProcessFunction extends KeyedProcessFunction<String, String, String> {

        @Override
        public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
            out.collect("Klucz: " + ctx.getCurrentKey());
            out.collect("Timestamp: " + ctx.timestamp());
            out.collect("Czas systemowy: " + ctx.timerService().currentProcessingTime());

            // Emitowanie do side output, jeśli wartość to "late"
            if ("late".equals(value)) {
                ctx.output(lateOutputTag, "LATE DATA: " + value);
            }
        }
    }
}
