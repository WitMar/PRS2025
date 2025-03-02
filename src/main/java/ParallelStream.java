import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class ParallelStream {
    static Logger log = LoggerFactory.getLogger(ParallelStream.class);
    static int i = 0;

    public static void main(String[] args) {

        IntStream stream = IntStream.range(1, 100);
        stream.parallel().forEach(number -> {
            if (number > i) {
                log.info(i + " smaller than " + number);
                i += 2;
            }
        });
        log.info("i at the end " + String.valueOf(i));

        //Sum from 1 to n in streams - this works properly
        int result = IntStream.rangeClosed(1, 15).parallel().reduce(0, (x, y) -> {
            log.info(String.valueOf(x + y));
            return x + y;
        });
        log.info(String.valueOf(result));
    }
}
