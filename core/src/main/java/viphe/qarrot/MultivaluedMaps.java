package viphe.qarrot;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultivaluedMaps {
    
    private static final class EmptyMultivaluedMap<K, V> extends AbstractMap<K, List<V>> implements MultivaluedMap<K, V> {

        private final Set<Entry<K, List<V>>> entrySet = Collections.emptySet();


        @Override
        public Set<Entry<K, List<V>>> entrySet() {
          return entrySet;
        }

        @Override
        public void putSingle(K key, V value) {
            throw new IllegalStateException("read-only");
        }

        @Override
        public void add(K key, V value) {
            throw new IllegalStateException("read-only");
        }

        @Override
        public V getFirst(K key) {
            return null;
        }
    }

    public static <K, V> MultivaluedMap<K, V> empty() {
        return new EmptyMultivaluedMap<K, V>();
    }
}
