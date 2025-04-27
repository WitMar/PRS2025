package gatherers;


import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class Scan {
    public static void main(String[] args) {
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);

        numbers.gather(Gatherers.scan(
                () -> 0,                           // początkowy stan
                Integer::sum      // akumulacja
                // zamiana stanu na wynik
        )).forEach(sum -> System.out.println("Suma bieżąca: " + sum));
    }
}

