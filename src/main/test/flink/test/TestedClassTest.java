package flink.test;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestedClassTest {

    @Test
    public void testChangeDetection() throws Exception {
        KeyedProcessFunction<String, String, String> function = new TestedClass();

        OneInputStreamOperatorTestHarness<String, String> testHarness =
                new KeyedOneInputStreamOperatorTestHarness<>(
                        new KeyedProcessOperator<>(function),
                        value -> value.split(":")[0],  // wyodrębnij sensorId jako klucz
                        Types.STRING
                );

        testHarness.open();

        // Pierwsza wartość dla sensor1
        testHarness.processElement("sensor1:open", 1000);
        // Taka sama wartość – brak zmiany
        testHarness.processElement("sensor1:open", 2000);
        // Zmiana wartości – powinno emitować
        testHarness.processElement("sensor1:click", 3000);

        List<String> output = testHarness.extractOutputValues();

        assertEquals(1, output.size());
        assertEquals("[sensor1] Zmieniono z open na click", output.get(0));
    }
}
