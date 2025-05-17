package flink.sensor;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionSensorAverageJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Dane sensorId : sectionId : wartosc
        DataStream<String> input = env.fromElements(
                "s1:A:10.0", "s2:A:11.0", "s3:B:20.0", "s1:A:12.0",
                "s2:A:13.0", "s3:B:19.0", "s1:B:14.0", "s2:B:15.0",
                "s3:A:16.0", "s1:A:17.0", "s2:A:18.0", "s1:A:19.0"
        );

        input.keyBy(v -> v.split(":")[1]).process(new SensorValueAggregator()).print();

        env.execute("Średnie sekcji z najnowszych wartości sensorów");
    }

    public static class SensorValueAggregator extends ProcessFunction<String, String> {

        // MapState: sensorId -> (sectionId, value)
        private transient MapState<String, Tuple2<String, Double>> latestSensorValues;

        @Override
        public void open(Configuration parameters) {
            MapStateDescriptor<String, Tuple2<String, Double>> descriptor =
                    new MapStateDescriptor<>(
                            "latest-sensor-values",
                            TypeInformation.of(String.class),
                            TypeInformation.of(new TypeHint<Tuple2<String, Double>>() {
                            })
                    );
            latestSensorValues = getRuntimeContext().getMapState(descriptor);
        }

        @Override
        public void processElement(String input, Context ctx, Collector<String> out) throws Exception {
            // Parsowanie wejścia
            String[] parts = input.split(":");
            String sensorId = parts[0];
            String sectionId = parts[1];
            double value = Double.parseDouble(parts[2]);

            // Aktualizacja ostatniej znanej wartości
            latestSensorValues.put(sensorId, Tuple2.of(sectionId, value));

            // Grupowanie: sectionId -> lista wartości sensorów
            Map<String, List<Double>> sectionMap = new HashMap<>();

            for (Map.Entry<String, Tuple2<String, Double>> entry : latestSensorValues.entries()) {
                Tuple2<String, Double> sensorInfo = entry.getValue();
                sectionMap.computeIfAbsent(sensorInfo.f0, k -> new ArrayList<>()).add(sensorInfo.f1);
            }

            // Obliczanie średnich
            for (Map.Entry<String, List<Double>> entry : sectionMap.entrySet()) {
                String sec = entry.getKey();
                List<Double> values = entry.getValue();
                double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                out.collect("Sekcja: " + sec + ", sensorów: " + values.size() + ", średnia: " + avg);
            }
        }
    }
}
