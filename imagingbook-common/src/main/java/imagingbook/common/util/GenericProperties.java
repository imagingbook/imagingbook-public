/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import java.util.HashMap;
import java.util.Objects;


/**
 * Defines a primitive mechanism for attaching arbitrary properties to an object dynamically using generic types to
 * eliminate type casts. Objects of any type may be attached and some type checking is done at compile time. The keys to
 * be used for inserting and retrieving values are identified by name (a {@link String}) and associated with a specific
 * value type. An implementing class only needs to define method {@link #getPropertyMap()}. In principle, the
 * functionality is the same as {@link HashMap} but definition as an interface avoids having to subclass
 * {@link HashMap}. Typical usage example:
 * <pre>
 *     public class Foo implements GenericProperties {
 *          private final PropertyMap properties = new PropertyMap();
 *          &#64;Override
 *          public PropertyMap getPropertyMap() {
 *              return this.properties;
 *          }
 *          ...
 *     }
 *     Foo f = new Foo();
 *     PropertyKey<double[]> key = new PropertyKey<>("UniqueName");
 *     double[] x = {1, 2, 3, 4};
 *     f.setProperty(key, x);
 *     ...
 *     double[] y = f.getProperty(key);
 * </pre>
 * Note that only values matching the key's type can be passed to {@link #setProperty(PropertyKey, Object)} and no type
 * casts are required when using {@link #getProperty(PropertyKey)}. However, creating duplicate keys with the same name
 * but different type is an error and must be avoided (unfortunately this cannot be checked at compile time and
 * {@link PropertyKey} has no information about the associated value class at runtime).
 *
 * @author WB
 * @version 2023/01/03
 */
public interface GenericProperties {

    /**
     * Defines a generic map key to be used with {@link GenericProperties}.
     * @param <T> the generic key type
     */
    public final class PropertyKey<T> {
        private final String name;

        public PropertyKey(String name) {
            this.name = name;
        }
    }

    /**
     * The underlying hash map class, to be instantiated by implementing classes.
     */
    public final class PropertyMap extends HashMap<String, Object> {
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
     * Associates the specified value with the specified key in this property map. The supplied value must match the
     * generic type of key. If the property map previously contained an entry for that key, the old value is replaced by
     * the specified value if it is of the same type as the new value. Otherwise, if the type of the existing entry is
     * different to the type of the new value, an exception is thrown. This happens when two {@link PropertyKey} with the
     * same name but of different value type are used (which is an error).
     *
     * @param key the key of the property (may not be {@code null})
     * @param value the value associated with this property (may not be {@code null})
     * @param <T> the generic key and value type
     * @throws IllegalArgumentException if the supplied key or value is {@code null} or if the property map already
     * contains an entry with a different type
     */
    public default <T> void setProperty(PropertyKey<T> key, T value) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("property key must not be null");
        }
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("property value must not be null");
        }
        PropertyMap map = getPropertyMap();
        Object oldval = map.get(key.name);
        if (oldval != null && !oldval.getClass().equals(value.getClass())) {
            throw new IllegalArgumentException("duplicate map key " + key.name + " with value of type " +
                            oldval.getClass().getSimpleName());
        }
        map.put(key.name, value);
    }

    /**
     * Returns the value associated with the specified {@link PropertyKey}, or {@code null} if this map contains no
     * mapping for the key.
     *
     * @param key the name of the property (may not be {@code null})
     * @param <T> the generic key and value type
     * @return the value (of type T) to which the specified key is mapped, or {@code null} if this map contains no
     * mapping for the key
     * @throws IllegalArgumentException if the supplied key is {@code null}
     */
    public default <T> T getProperty(PropertyKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("property key must not be null");
        }
        return (T) getPropertyMap().get(key.name);
    }

    /**
     * Removes the property associated with the specified key if defined, otherwise does nothing.
     * @param key the name of the property
     * @param <T> the generic key type
     */
    public default <T> void removeProperty(PropertyKey<T> key) {
        getPropertyMap().remove(key.name);
    }

    /**
     * Removes all properties.
     */
    public default void clearAllProperties() {
        getPropertyMap().clear();
    }

}
