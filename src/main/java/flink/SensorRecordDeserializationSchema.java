package flink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class SensorRecordDeserializationSchema implements DeserializationSchema<SensorRecord> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SensorRecord deserialize(byte[] message) throws IOException {
        try {
            return objectMapper.readValue(message, SensorRecord.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isEndOfStream(SensorRecord nextElement) {
        return false;
    }

    @Override
    public TypeInformation<SensorRecord> getProducedType() {
        return TypeInformation.of(SensorRecord.class);
    }
}
