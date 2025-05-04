package flink.operators;


import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StreamAverageOperator {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> input = env.fromElements(
                Tuple2.of("A", 1),
                Tuple2.of("A", 2),
                Tuple2.of("A", 3),
                Tuple2.of("B", 4),
                Tuple2.of("B", 6),
                Tuple2.of("A", 4)
        );

        input
                .keyBy(t -> t.f0)
                .flatMap(new AverageCalculator())  // obliczamy średnią z 3 ostatnich wartości
                .print();

        env.execute("Średnia ze strumienia po kluczu");
    }

    public static class AverageCalculator extends RichFlatMapFunction<Tuple2<String, Integer>, Tuple2<String, Double>> {
        // (suma, licznik)
        private transient ValueState<Tuple2<Long, Long>> state;

        public AverageCalculator() {
        }

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor<Tuple2<Long, Long>> descriptor =
                    new ValueStateDescriptor<>(
                            "sumAndCount",
                            Types.TUPLE(Types.LONG, Types.LONG),
                            Tuple2.of(0L, 0L)
                    );
            state = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void flatMap(Tuple2<String, Integer> input, Collector<Tuple2<String, Double>> out) throws Exception {
            Tuple2<Long, Long> current = state.value();
            current.f0 += input.f1;  // suma
            current.f1 += 1;         // licznik

            double avg = current.f0 / (double) current.f1;
            out.collect(Tuple2.of(input.f0, avg));

            state.update(current);
        }
    }
}

