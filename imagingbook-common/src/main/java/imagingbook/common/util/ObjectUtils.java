/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Defines static methods for object copying and encoding to strings.
 * 
 * @author WB
 * @version 2022/09/11
 */
public abstract class ObjectUtils {
	
	private ObjectUtils() {
	}
	
	// https://stackoverflow.com/a/26000025
	
	/**
	 * Returns a shallow copy of the given object using reflection.
	 * This can be used if the {@link Cloneable} interface is not 
	 * (or cannot be) implemented.
	 * 
	 * @param <T> generic type
	 * @param entity the object to be copied
	 * @return the object copy
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T entity) {
	    Class<?> clazz = entity.getClass();
//	    T newEntity = (T) entity.getClass().newInstance();
	    T newEntity = null;
		try {
			newEntity = (T) entity.getClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) { }

	    while (clazz != null) {
	        copyFields(entity, newEntity, clazz);
	        clazz = clazz.getSuperclass();
	    }

	    return newEntity;
	}

	private static <T> T copyFields(T entity, T newEntity, Class<?> clazz) {
	    List<Field> fields = new ArrayList<>();
	    for (Field field : clazz.getDeclaredFields()) {
	        fields.add(field);
	    }
	    for (Field field : fields) {
	        field.setAccessible(true);
	        try {
				field.set(newEntity, field.get(entity));
			} catch (IllegalArgumentException | IllegalAccessException e) { }
	    }
	    return newEntity;
	}
	
	// --------------------------------------------------------------------------------
	
	// adapted from https://stackoverflow.com/a/30968827
	private static byte[] convertToBytes(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
	        out.writeObject(object);
	        return bos.toByteArray();
	    } catch (IOException e) { }
	    return null;
	}
	
	private static Object convertFromBytes(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	    try (ObjectInputStream in = new ObjectInputStream(bis)) {
	        return in.readObject();
	    } catch (IOException | ClassNotFoundException e) { }
	    return null;
	}
	
	
	// TODO: check for null/exceptions
	/**
	 * Serializes and encodes an arbitrary object to a string. This is potentially
	 * dangerous and only intended for local use, e.g., to define constant matrices
	 * with full precision. The string encoding is 'Base64' implemented with
	 * standard Java8 functionality. See {@link #decodeFromString(String)} for
	 * decoding.
	 * 
	 * @param object an arbitrary object
	 * @return the Base64 string encoding of the object
	 */
	public static String encodeToString(Object object) {
		byte[] ba = convertToBytes(object);
		return Base64.getEncoder().encodeToString(ba);
	}
	
	// TODO: check for null/exceptions
	/**
	 * Decodes and deserializes an object encoded with {@link #encodeToString(Object)}.
	 * The type of the encoded object must be known.
	 * 
	 * @param string the encoded object string
	 * @return the associated object (cast to the known type)
	 */
	public static Object decodeFromString(String string) {
		byte[] ba = Base64.getDecoder().decode(string);
		return convertFromBytes(ba);
	}

}
