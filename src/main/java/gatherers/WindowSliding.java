package gatherers;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class WindowSliding {
    public static void main(String[] args) {
        Stream<Integer> numbers = Stream.of(2, 3, 5, 4, 6, 7);

        numbers.gather(Gatherers.windowSliding(3))
                .filter(window -> window.get(0) < window.get(1) && window.get(1) < window.get(2))
                .forEach(window -> System.out.println("Okno rosnące: " + window));
    }
}
