package gatherers;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class Fold {
    public static void main(String[] args) {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);

        int sum = numbers.gather(Gatherers.fold(
                () -> 0,                          // początkowy stan
                Integer::sum     // operacja na stanie
                // finalizacja
        )).findFirst().orElse(0);

        System.out.println("Suma wszystkich elementów: " + sum);
    }
}

