package flink;

public class SensorRecord {
    public int sensorId;
    public String valueType;
    public double value;
    public double latitude;
    public double longitude;
    public String timestamp; // or use java.time.LocalDateTime if preferred

    @Override
    public String toString() {
        return "SensorRecord{" +
                "sensorId=" + sensorId +
                ", valueType='" + valueType + '\'' +
                ", value=" + value +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}