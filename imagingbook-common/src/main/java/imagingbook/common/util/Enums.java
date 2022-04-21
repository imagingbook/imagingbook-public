/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import imagingbook.common.corners.subpixel.SubpixelMaxInterpolator;

/**
 * This class defines static methods for handling enum types.
 * @author WB
 *
 */
@SuppressWarnings("unused")
public abstract class Enums {

	/**
	 * This static method returns an array of all constant names (strings) 
	 * for a given enumeration class. 
	 * Names are arranged in the same order as the enum constant definitions.
	 * Assume the enum definition: 
	 * <pre>enum MyEnum {A, B, C};</pre>
	 * Usage: 
	 * <pre>String[] names = getEnumNames(MyEnum.class);</pre>
	 * This returns the array 
	 * <pre>{"A", "B", "C"}</pre>
	 * 
	 * @param <E> the generic enum type
	 * @param enumclass enumeration class
	 * @return array (possibly empty) of names defined for the specified enumeration class
	 */
	public static <E extends Enum<E>> String[] getEnumNames(Class<E> enumclass) {
		E[] eConstants = enumclass.getEnumConstants();
		String[] eNames = new String[eConstants.length];
		for (int i = 0; i < eConstants.length; i++) {
			eNames[i] = eConstants[i].name();
		}
		return eNames;
	}
//	public static String[] getEnumNames(Class<? extends Enum<?>> enumclass) {
//		Enum<?>[] eConstants = enumclass.getEnumConstants();
//		String[] eNames = new String[eConstants.length];
//		for (int i = 0; i < eConstants.length; i++) {
//			eNames[i] = eConstants[i].name();
//		}
//		return eNames;
//	}
	
	// ---------------------------------------------------------------
	
	/**
	 * Field annotation with a (required) string parameter to supply a descriptive
	 * text to the associated enum constant.
	 */
	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Description {
		public String value(); // no default, value must be supplied!
	}
	
	/**
	 * This static method returns an array of all description strings 
	 * attached as {@link Description} annotations to enum-constants
	 * in the given enumeration class.
	 * For non-annotated constants the constants name is used as a substitute.
	 * Descriptions are arranged in the same order as the enum constant definitions.
	 * Usage example:
	 * <pre>
	 * public enum EnumWithAnnotations {
	 * 	{@literal @}Description("Abra Kadabra") A, 
	 * 	{@literal @}Description("Bubba Can Walk") B,
	 * 	C;
	 * }</pre>
	 * This returns the array 
	 * <pre>{"Abra Kadabra", "Bubba Can Walk", "C"}</pre>
	 * 
	 * @param enumClass the enumeration class
	 * @return array (possibly empty) of descriptions defined for the specified enumeration class
	 */
	public static String[] getEnumDescriptions(Class<? extends Enum<?>> enumClass) {
		Field[] declaredFields = enumClass.getDeclaredFields();
		List<String> descList = new LinkedList<>();
		for (Field df : declaredFields) {
			if (df.isEnumConstant()) {
				Description desc = df.getDeclaredAnnotation(Description.class);
				String descString = (desc != null) ? desc.value() : df.getName();
				descList.add(descString);
			}
		}
		return descList.toArray(new String[0]);
	}
	
	/**
	 * Returns an {@link EnumMap} with the enum constants as the keys and the 
	 * associated descriptions (of type {@link String}) as the values.
	 * Descriptions are specified by annotating enum fields (see {@link Description}).
	 * For non-annotated constants the constants name is used as a substitute.
	 * Keys and values are arranged in the same order as the enum constant definitions.
	 * This is experimental code (currently unused).
	 * 
	 * @param <E> the generic enumeration type
	 * @param enumClass the enumeration class
	 * @return a map as described
	 */
	public static <E extends Enum<E>> EnumMap<E, String> getEnumDescriptionsMap(Class<E> enumClass) {
		EnumMap<E, String> map = new EnumMap<E, String>(enumClass);
		Field[] declaredFields = enumClass.getDeclaredFields();
		for (Field df : declaredFields) {
			if (df.isEnumConstant()) {
				E enumConst = Enum.valueOf(enumClass, df.getName());
				Description desc = df.getDeclaredAnnotation(Description.class);
				String descString = (desc != null) ? desc.value() : df.getName();
				map.put(enumConst, descString);
			}
		}
		return map;
	}

	
	// ---------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Class<? extends Enum<?>> enumClass;
//		
//		enumClass = SubpixelMaxInterpolator.Method.class;
//		System.out.println("*** enumClass = " + enumClass.getSimpleName());
//		for (String d : Enums.getEnumNames(enumClass)) {
//			System.out.println(d);
//		}
//		for (String d : Enums.getEnumDescriptions(enumClass)) {
//			System.out.println(d);
//		}
//		
//		enumClass = EnumWithAnnotationsExample.class;
//		System.out.println("*** enumClass = " + enumClass.getSimpleName());
//		for (String d : Enums.getEnumNames(enumClass)) {
//			System.out.println(d);
//		}
//		for (String d : Enums.getEnumDescriptions(enumClass)) {
//			System.out.println(d);
//		}
//		
//		
//		System.out.println("*** map test:");
//		EnumMap<? extends Enum<?>, String> map = getEnumDescriptionsMap(EnumWithAnnotations.class);
//		for (Enum<?> m : map.keySet()) {
//			System.out.println(m.name() + " -> " + map.get(m));
//		}
//	}
	
}
