package imagingbook.common.util;

import java.util.HashMap;
import java.util.Objects;

/**
 * Defines a primitive mechanism for attaching arbitrary properties to an object at runtime. Objects of any type may be
 * attached and no type checking is done at compile time, i.e., the required type casts are inherently unsafe. An
 * implementing class only needs to define method {@link #getPropertyMap()}. The functionality is basically the same as
 * {@link HashMap} but definition as an interface avoids having to subclass {@link HashMap}. Usage example:
 * <pre>
 *     public class Foo implements DynamicProperties {
 *         ...
 *     }
 *
 *     Foo f = new Foo();
 *     double[] x = {1, 2, 3, 4};
 *     f.setProperty("SomeDoubleArray", x);
 *     ...
 *     double[] y = (double[]) f.getProperty("SomeDoubleArray");
 * </pre>
 */
public interface DynamicProperties {

    /**
     * The underlying hash map, to be instantiated by implementing classes.
     */
    public class PropertyMap extends HashMap<String, Object> {
        public PropertyMap() {
            super(4);   // start with up to 4 properties
        }
    }

    /**
     * Returns an instance of {@link PropertyMap} with keys of type {@link String} and values of type {@link Object}.
     * Implementing classes must define this method, which will typically return a reference to a local map instance.
     * The returned object must not be {@code null}.
     *
     * @return the {@link PropertyMap} associated with the implementing instance
     */
    public PropertyMap getPropertyMap();

    /**
     * Sets the specified property of this region to the given value.
     * @param key the key of the property (may not be {@code null})
     * @param value the value associated with this property (may not be {@code null})
     * @throws IllegalArgumentException if the supplied key or value is {@code null}
     */
    public default void setProperty(String key, Object value) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("property key must not be null");
        }
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("property value must not be null");
        }
        getPropertyMap().put(key, value);
    }

    /**
     * Retrieves the specified region property. {@link IllegalArgumentException} is thrown if the property is not
     * defined.
     *
     * @param key the name of the property (may not be {@code null})
     * @return the value of the associated property
     * @throws IllegalArgumentException if the supplied key is {@code null}
     */
    public default Object getProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("property key must not be null");
        }
        return getPropertyMap().get(key);
    }

    /**
     * Removes the property associated with the specified key if defined, otherwise does nothing.
     * @param key the name of the property
     */
    public default void removeProperty(String key) {
        getPropertyMap().remove(key);
    }

    /**
     * Removes all properties.
     */
    public default void clearAllProperties() {
        getPropertyMap().clear();
    }

}
