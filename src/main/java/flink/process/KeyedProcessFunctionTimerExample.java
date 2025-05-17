package flink.process;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class KeyedProcessFunctionTimerExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // Sztuczne zdażenia wywołujące przetwarzanie
        env.getConfig().setAutoWatermarkInterval(1000);
        env.getConfig().setLatencyTrackingInterval(1000);


        // Ustawienie strumienia wejściowego
        DataStream<String> input = env.fromElements("hello", "alert", "world");

        // Strumień musi być kluczowany, np. stały klucz "key"
        DataStream<String> result = input
                .keyBy(value -> "key") // wymagane do obsługi timerów
                .process(new MyKeyedProcessFunction());

        result.print();

        env.execute("Flink KeyedProcessFunction Timer Example");
    }

    // Zamiast ProcessFunction używamy KeyedProcessFunction
    public static class MyKeyedProcessFunction extends KeyedProcessFunction<String, String, String> {

        @Override
        public void processElement(String value, Context ctx, Collector<String> out) {
            if ("alert".equals(value)) {
                long triggerTime = ctx.timerService().currentProcessingTime() + 1000;
                ctx.timerService().registerProcessingTimeTimer(triggerTime);
                out.collect("Zarejestrowano timer na: " + triggerTime);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            out.collect("Odebrano: " + value);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) {
            out.collect("Timer wyzwolony o czasie: " + timestamp);
        }
    }
}
