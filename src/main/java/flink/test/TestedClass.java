package flink.test;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class TestedClass extends KeyedProcessFunction<String, String, String> {

    private transient ValueState<String> lastSeenState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<String> descriptor = new ValueStateDescriptor<>(
                "lastSeen", String.class);
        lastSeenState = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
        String action = value.split(":")[1];
        String last = lastSeenState.value();

        if (last != null && !last.equals(action)) {
            out.collect("[" + ctx.getCurrentKey() + "] Zmieniono z " + last + " na " + action);
        }

        lastSeenState.update(action);
    }
}
