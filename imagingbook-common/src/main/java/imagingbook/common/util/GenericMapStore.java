package imagingbook.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class defines a generic storage container that is internally based on a hash map. A ordinary Java
 * {@link java.util.HashMap} can hold only items (values) of the type (T) that was specified at construction. In
 * contrast, {@link GenericMapStore} may hold values of any object type, but the type is coupled to the supplied key.
 * That is, the supplied key specifies the type of the associated value. This makes it possible to insert and retrieve
 * arbitrary objects safely and without any type casts.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <pre>
 *         GenericMapStore gms = new GenericMapStore();
 *         
 *         // define keys for various value types:
 *         MapKey&lt;Double&gt; doubleKey1 = gms.newKey();
 *         MapKey&lt;Double&gt; doubleKey2 = gms.newKey();
 *         MapKey&lt;double[][]&gt; doubleArrayKey = gms.newKey();
 *         MapKey&lt;String&gt; stringKey = gms.newKey();
 *
 *         // insert values:
 *         gms.put(doubleKey1, 9.9);
 *         gms.put(doubleKey2, Math.PI);
 *         gms.put(doubleArrayKey, new double[][] {{3}, {4}});
 *         gms.put(stringKey, "Foo");
 *         gms.put(stringKey, "Bar");   // overwrite existing item
 *
 *         // retrieve values
 *         System.out.println(gms.get(doubleKey1));
 *         System.out.println(gms.get(doubleKey2));
 *         System.out.println(Matrix.toString(gms.get(doubleArrayKey)));
 *         System.out.println(gms.get(stringKey));
 * </pre>
 */
public class GenericMapStore {

    private int keyCnt = 0;
    private final Map<Integer, Object> map;

    /**
     * Constructor.
     */
    public GenericMapStore() {
        map = new HashMap<>();
    }

    /**
     * Creates and returns a new, unique key of the specified type (T) which can be subsequently used to insert map
     * values of this type.
     *
     * @param <T> the generic element type
     * @return a new key for the specified element type
     */
    public <T> MapKey<T> newKey(){
        return new MapKey<>(keyCnt++);
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously contained a mapping for
     * that key, the old value is replaced by the specified value. The supplied value must match the generic type of
     * key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param <T> the generic key and value type
     * @see Map#put(Object, Object)
     */
    public <T> void put(MapKey<T> key, T value) {
        map.put(key.id, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
     * key.
     *
     * @param key the key whose associated value is to be returned
     * @param <T> the generic key and value type
     * @return the value (of type T) to which the specified key is mapped, or {@code null} if this map contains no
     * mapping for the key
     * @see Map#get(Object)
     */
    public <T> T get(MapKey<T> key) {
        T val = (T) map.get(key.id);
        return val;
    }

    /**
     * Returns the number of key-value mappings in this container.
     *
     * @return the number of key-value mappings in this map
     * @see Map#size()
     */
    public int size() {
        return map.size();
    }

    /**
     * Removes all key/value mappings from this container.
     * @see Map#clear()
     */
    public void clear() {
        map.clear();
    }
    
    // -------------------------------------------------------------

    /**
     * Defines a generic map key to be used with {@link GenericMapStore}.
     * @param <T> the generic key type
     */
    public class MapKey<T> {
        private Integer id;
        private MapKey(int id) {
            this.id = id;
        }
    }

    // -------------------------------------------------------------

    // public static void main(String[] args) {
    //     GenericMapStore gms = new GenericMapStore();
    //
    //     MapKey<Double> doubleKey1 = gms.newKey();
    //     MapKey<Double> doubleKey2 = gms.newKey();
    //     MapKey<double[][]> doubleArrayKey = gms.newKey();
    //     MapKey<String> stringKey = gms.newKey();
    //
    //     gms.put(doubleKey1, 9.9);
    //     gms.put(doubleKey2, Math.PI);
    //     gms.put(doubleArrayKey, new double[][] {{3}, {4}});
    //     gms.put(stringKey, "Foo");
    //     gms.put(stringKey, "Bar");   // overwrite existing item
    //
    //     System.out.println(gms.get(doubleKey1));
    //     System.out.println(gms.get(doubleKey2));
    //     System.out.println(Matrix.toString(gms.get(doubleArrayKey)));
    //     System.out.println(gms.get(stringKey));
    // }

}
