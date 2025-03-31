package collections;

import org.slf4j.LoggerFactory;
import threadgroup.Groups;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ConcurrentMaps {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Groups.class);

    public static void main(String[] args) {
        ConcurrentHashMap<Integer,String> map = new ConcurrentHashMap<Integer,String>();

        map.put(1,"value");
        map.remove(1,"valueNew");
        logger.info(String.valueOf(map.size()));
        map.remove(1,"value");
        logger.info(String.valueOf(map.size()));

        logger.info(String.valueOf(map.searchValues(2L, (Function<? super String, ? extends Boolean>) s -> s.equals("value"))));
        map.put(2,"value");
        logger.info(String.valueOf(map.searchValues(2L, (Function<? super String, ? extends Boolean>) s -> s.equals("value"))));

    }
}
