package gatherers;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class WindowFixed {
    public static void main(String[] args) {
        Stream<Integer> numbers = Stream.iterate(1, n -> n + 1).limit(10);

        numbers.gather(Gatherers.windowFixed(4))
                .map(window -> window.stream().mapToInt(Integer::intValue).average().orElse(0))
                .forEach(avg -> System.out.println("Średnia: " + avg));
    }
}
