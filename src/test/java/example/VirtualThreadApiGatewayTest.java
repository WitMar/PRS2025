package example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VirtualThreadApiGatewayTest {

    @Test
    public void testApiGatewaySingleRequest() throws IOException {
        URL url = new URL("http://localhost:8181/api");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void benchmarkMultipleRequests() throws InterruptedException {
        int requestCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    URL url = new URL("http://localhost:8181/api");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Handled " + requestCount + " requests in " + elapsedTime + " ms");
    }
}