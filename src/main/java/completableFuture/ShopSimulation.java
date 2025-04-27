package completableFuture;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ShopSimulation {

    public static void main(String[] args) {
        System.out.println("Sklep online - rozpoczęcie procesu zamówienia...");

        String product = "Laptop";

        CompletableFuture<Void> orderProcess = checkStock(product)
                .thenCompose(stockAvailable -> {
                    if (stockAvailable) {
                        return processPayment(product);
                    } else {
                        throw new RuntimeException("Produkt niedostępny w magazynie.");
                    }
                })
                .thenCompose(paymentSuccessful -> {
                    if (paymentSuccessful) {
                        return prepareShipment(product);
                    } else {
                        throw new RuntimeException("Płatność nie powiodła się.");
                    }
                })
                .thenAccept(shippingLabel -> {
                    System.out.println("Wysłano przesyłkę: " + shippingLabel);
                })
                .exceptionally(error -> {
                    System.out.println("Błąd realizacji zamówienia: " + error.getMessage());
                    return null;
                });

        orderProcess.join(); // Czekamy na zakończenie procesu
        System.out.println("Koniec symulacji.");
    }

    // Etap 1: Sprawdzenie dostępności magazynu
    private static CompletableFuture<Boolean> checkStock(String product) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay("Sprawdzanie magazynu", 1, 3);
            boolean available = ThreadLocalRandom.current().nextBoolean();
            System.out.println("Produkt '" + product + "' dostępny: " + available);
            return available;
        });
    }

    // Etap 2: Przetwarzanie płatności
    private static CompletableFuture<Boolean> processPayment(String product) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay("Przetwarzanie płatności", 1, 2);
            boolean paymentOk = ThreadLocalRandom.current().nextBoolean();
            System.out.println("Płatność za '" + product + "' zakończona sukcesem: " + paymentOk);
            return paymentOk;
        });
    }

    // Etap 3: Przygotowanie wysyłki
    private static CompletableFuture<String> prepareShipment(String product) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay("Przygotowanie przesyłki", 2, 4);
            String shippingLabel = "Etikieta_wysyłki_" + product.toUpperCase();
            return shippingLabel;
        });
    }

    // Funkcja pomocnicza: Symulacja opóźnienia
    private static void simulateDelay(String action, int minSeconds, int maxSeconds) {
        int delay = ThreadLocalRandom.current().nextInt(minSeconds, maxSeconds + 1);
        System.out.println(action + " (oczekiwanie " + delay + " sekundy/-und)");
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

