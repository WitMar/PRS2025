package flink.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class GlobalAverageOperator {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Przykładowe dane wejściowe
        DataStream<Integer> input = env.fromElements(1, 2, 3, 4, 5, 6);

        input
                .keyBy(a -> 1)
                .map(new GlobalAverage())
                .print();

        env.execute("Średnia dla całego strumienia");
    }

    // Operator obliczający średnią globalnie
    public static class GlobalAverage extends RichMapFunction<Integer, Double> {

        private transient ValueState<Tuple2<Long,Long>> sumAndCount;

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor<Tuple2<Long,Long>> descriptor =
                    new ValueStateDescriptor<>(
                            "sumAndCount",
                            Types.TUPLE(Types.LONG, Types.LONG),
                            Tuple2.of(0L,0L)
                    );

            sumAndCount = getRuntimeContext().getState(descriptor);
        }

        @Override
        public Double map(Integer value) throws Exception {
            Tuple2<Long,Long> current = sumAndCount.value();
            current.f0 += value; // suma
            current.f1 += 1;     // licznik

            sumAndCount.update(current);
            return current.f0 / (double) current.f1;
        }
    }
}

