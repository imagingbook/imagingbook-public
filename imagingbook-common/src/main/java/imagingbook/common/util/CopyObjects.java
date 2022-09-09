package imagingbook.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines static methods for making a shallow copy of any object.
 * @author WB
 *
 */
public abstract class CopyObjects {
	
	private CopyObjects() {
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

}
