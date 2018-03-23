import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapBuilder<K, V> {

    public enum Configuration {
        OVERWRITE, DONT_OVERWRITE
    }

    private Stream<Map.Entry<K, V>> mapStream;
    private Configuration configuration;

    public MapBuilder() {
        this(Configuration.OVERWRITE);
    }

    public MapBuilder(Configuration configuration) {
        this.mapStream = Stream.empty();
        this.configuration = configuration;
    }

    public MapBuilder<K, V> put(K key, V value) {
        this.mapStream = Stream.concat(this.mapStream, entryStream(key, value));
        return this;
    }

    public MapBuilder<K, V> put(List<K> keys, V value) {
        Stream<Map.Entry<K, V>> entryStream = keys.stream().map(key -> entry(key, value));
        this.mapStream = Stream.concat(this.mapStream, entryStream);
        return this;
    }

    private Stream<Map.Entry<K, V>> entryStream(K key, V value) {
        return Stream.of(entry(key, value));
    }

    private AbstractMap.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<K, V>(key, value);
    }

    public Map<K, V> build() {
        Map<K, V> tmpMap = this.mapStream.collect(toMap());
        return Collections.unmodifiableMap(tmpMap);
    }

    private Collector<Map.Entry<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(e -> e.getKey(), e -> e.getValue(), applyConfiguration());
    }

    private BinaryOperator<V> applyConfiguration() {
        return (former, later) -> {
            if (configuration == Configuration.OVERWRITE) {
                return later;
            } else {
                return former;
            }
        };
    }
}