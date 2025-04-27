package example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadApiGateway {
    public static void main(String[] args) throws IOException {
        int port = 8181;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler());
        ExecutorService virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();
        server.setExecutor(virtualThreadPool);
        server.start();
        System.out.println("API Gateway started on port " + port);
    }

    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                simulateBackendCall();
                String response = "Response from API Gateway at " + Thread.currentThread();
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (InterruptedException e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            }
        }

        private void simulateBackendCall() throws InterruptedException {
            Thread.sleep(500);
        }
    }
}