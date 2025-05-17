package flink.state;

import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class ValueState {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Przykładowe dane: dla różnych użytkowników (klucze)
        DataStream<String> input = env.fromElements(
                "user1:open",
                "user2:open",
                "user1:click",
                "user2:scroll",
                "user2:scroll",
                "user2:click",
                "user1:click"
        );

        // Kluczowanie po nazwie użytkownika i wykrywanie zmian
        input
                .keyBy(value -> value.split(":")[0])  // klucz to np. "user1", "user2"
                .process(new ChangeDetectionFunction())
                .print();

        env.execute("Change Detection with ValueState");
    }

    // Funkcja wykrywająca zmianę stanu per klucz
    public static class ChangeDetectionFunction extends KeyedProcessFunction<String, String, String> {

        private transient org.apache.flink.api.common.state.ValueState<String> lastSeenState;

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor<String> descriptor = new ValueStateDescriptor<>(
                    "lastSeen", String.class);
            lastSeenState = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
            String action = value.split(":")[1]; // np. "click", "scroll"
            String last = lastSeenState.value();

            if (last != null && !last.equals(action)) {
                out.collect("[" + ctx.getCurrentKey() + "] Zmieniono z " + last + " na " + action);
            }
            // Aktualizacja stanu dla klucza
            lastSeenState.update(action);
        }
    }
}

